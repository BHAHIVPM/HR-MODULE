package com.bhahi.hrmodule.jwt;

import com.bhahi.hr.exception.CustomException;
import com.bhahi.hrmodule.databasemapping.DatabaseContextHolder;
import com.bhahi.hrmodule.model.UserLogin;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserLogin currentUser;

    private final JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only filter the login endpoint
        return (("/auth/login".equals(request.getRequestURI())
                && "POST".equalsIgnoreCase(request.getMethod()))
                ||
                ((request.getRequestURI().contains("auth/guest-token"))
                        && "POST".equalsIgnoreCase(request.getMethod())));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        // ==============================================================================================================
        // THIS WILL OMIT THE JWT FILTER FOR SWAGGER/API-DOC PATHS.
        // ==============================================================================================================
        if (isIgnorePath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // ==============================================================================================================
            // EXTRACT TOKEN AND USER ID FROM COOKIES
            // ==============================================================================================================
            String token = getJwtFromCookies(request);
            String id = (token != null) ? jwtUtils.extractUsername(token) : null;

            if (token == null || id == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // ==============================================================================================================
            // PROCESS DATABASE ROUTING AND SECURITY CONTEXT
            // ==============================================================================================================
            processAuthenticationAndContext(request, token, id);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new CustomException("Authentication failed.", e.getMessage(), 500);
        } finally {
            DatabaseContextHolder.clear();
        }
    }

// ==============================================================================================================
// HELPER: CHECKS IF THE URI SHOULD BYPASS THE FILTER
// ==============================================================================================================
    private boolean isIgnorePath(String uri) {
        return uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-resources") ||
                uri.startsWith("/webjars");
    }

// ==============================================================================================================
// HELPER: EXTRACTS JWT FROM AUTHORIZED COOKIE NAMES
// ==============================================================================================================
    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if ("Access_token".equals(name) || "Guest_Token".equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

// ==============================================================================================================
// HELPER: HANDLES ROLES, DATABASE ROUTING, AND SPRING SECURITY AUTHENTICATION
// ==============================================================================================================
    private void processAuthenticationAndContext(HttpServletRequest request, String token, String id) {
        Claims claims = jwtUtils.extractType(token);

        // Extract Roles/Authorities
        List<String> roles = jwtUtils.extractRole(token);
        List<SimpleGrantedAuthority> authorities = (roles == null) ? List.of() :
                roles.stream().map(SimpleGrantedAuthority::new).toList();

        // Set Database Context
        String database = claims.get("database", String.class);
        if (database != null) {
//            DatabaseContextHolder.set(DatabaseType.valueOf(database));
            DatabaseContextHolder.set(database);
        }

        authOrGuest(claims, authorities);

// Authenticate if no existing context
        if (SecurityContextHolder.getContext().getAuthentication() == null && jwtUtils.isValidToken(token)) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(id, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authToken);
            SecurityContextHolder.setContext(securityContext);
        }
    }





// ==============================================================================================================
//      USED TO FIND THE TOKEN TYPE WHETHER IT IS AUTH OR GUEST.
// ==============================================================================================================

    private void authOrGuest(Claims claims, List<SimpleGrantedAuthority> authorities){
        String type=claims.get("type", String.class);

        UsernamePasswordAuthenticationToken auth;

        if ("AUTH".equals(type)) {
            auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, authorities);
            auth.setDetails("AUTH");
        } else if ("GUEST".equals(type)) {
            auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, authorities);
            auth.setDetails("GUEST"); // mark guest
        } else {
            auth = null;
        }

        if (auth != null) {
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }


}
