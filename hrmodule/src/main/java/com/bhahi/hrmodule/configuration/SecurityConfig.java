package com.bhahi.hrmodule.configuration;

import com.bhahi.hrmodule.databasemapping.DbRoutingPreAuthFilter;
import com.bhahi.hrmodule.jwt.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.function.Supplier;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final Logger logger= LoggerFactory.getLogger(SecurityConfig.class);
    private static final String UNAUTHORIZED_JSON = """
{
  "status": 401,
  "error": "Unauthorized"
}
""";

    private final JwtFilter jwtFilter;
    private final DbRoutingPreAuthFilter dbRoutingPreAuthFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(csrf -> csrf.disable()) // Disable CSRF for endpoint testing
            .authorizeHttpRequests(auth -> auth
                    // OPEN APIs — do NOT require JWT
                    .requestMatchers(
                            "/open/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**",
                            "/error"
                    ).permitAll()
                    .requestMatchers("/guest/**")
                    .access((authSupplier, context) -> {
                        Authentication authen = authSupplier.get();

                        logger.info("Access check for: {}", context.getRequest().getRequestURI());
                        if (authen == null) {
                            logger.info("Authentication is null → anonymous request");
                        } else {
                            logger.info("Authenticated user: {}", authen.getName());
                            logger.info("Details/type: {}", authen.getDetails());
                            logger.info("Authorities: {}", authen.getAuthorities());
                        }

                        return guestOrAuth(authSupplier, context); // call your existing logic
                    })

                    // AUTH token only
                    .anyRequest()
                    .access(this::authOnly)
            )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(UNAUTHORIZED_JSON.formatted(request.getRequestURI()));

                }))
                .addFilterBefore(dbRoutingPreAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();

    }

    private AuthorizationDecision guestOrAuth(Supplier<? extends Authentication> authentication,
                                              RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        if (auth == null) return new AuthorizationDecision(false);

        Object type = auth.getDetails();
        return new AuthorizationDecision(
                type == null || "GUEST".equals(type) || "AUTH".equals(type)
        );
    }

    private AuthorizationDecision authOnly(Supplier<? extends Authentication> authentication,
                                           RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        if (auth == null) return new AuthorizationDecision(false);

        Object type = auth.getDetails();
        return new AuthorizationDecision("AUTH".equals(type));
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }



}
