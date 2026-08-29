package com.bhahi.hrmodule.databasemapping;


import com.bhahi.hrmodule.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.sql.DataSource;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DbRoutingPreAuthFilter extends OncePerRequestFilter {

    private final ClientRoutingService clientRoutingService;
    private final RoutingDataSource routingDataSource;
    private final JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String uri = request.getRequestURI();
            String clientId = null;

            // ✅ CASE 1 — login / guest-token APIs: no cookie exists yet, so the loginId
            // travels in the URL path itself and we pull the client id straight from it.
            if (uri.startsWith("/auth/guest-token/") || uri.startsWith("/auth/login/")) {

                String loginId = uri.substring(uri.lastIndexOf("/") + 1);
                if (loginId.length() >= 4) {
                    clientId = loginId.substring(0, 4);
                }
            }

            // ✅ CASE 2 — other APIs
            else {

                if (request.getCookies() != null) {

                    for (Cookie cookie : request.getCookies()) {

                        String token = null;

                        if ("Guest_Token".equals(cookie.getName())) {
                            token = cookie.getValue();
                        }

                        if ("Access_token".equals(cookie.getName())) {
                            token = cookie.getValue();
                        }

                        if (token != null) {

                            clientId = jwtUtils.extractType(token)
                                    .get("database", String.class);

                            break;
                        }
                    }
                }
            }

            // ✅ Set datasource
            if (clientId != null) {

                DatabaseContextHolder.set(clientId);

                DataSource ds = clientRoutingService.getClientDataSource(clientId);

                routingDataSource.addDataSource(clientId, ds);
            }

            filterChain.doFilter(request, response);

        } finally {
            DatabaseContextHolder.clear();
        }
    }
}
