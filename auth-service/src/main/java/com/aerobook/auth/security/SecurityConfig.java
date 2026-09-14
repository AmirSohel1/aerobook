package com.aerobook.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ============================================================================
 * Spring Security 6 Configuration for Auth Service
 * ============================================================================
 *
 * Configures stateless security architecture, password hashing, and endpoint
 * authorization:
 * <ul>
 * <li><b>Stateless Sessions:</b> Disables HTTP sessions; authentication is
 * strictly token-based.</li>
 * <li><b>CSRF Disabled:</b> Cross-Site Request Forgery is disabled for
 * stateless REST endpoints.</li>
 * <li><b>Public Paths:</b> Permits {@code /api/auth/**}, Swagger UI
 * ({@code /swagger-ui/**}), and OpenAPI documentation endpoints
 * ({@code /v3/api-docs/**}).</li>
 * <li><b>BCrypt Password Encoder:</b> Uses standard 10-round BCrypt
 * cryptographic password hashing.</li>
 * </ul>
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures Spring Security filter chain for stateless REST operations.
     *
     * @param http Spring HttpSecurity builder
     * @return constructed {@link SecurityFilterChain}
     * @throws Exception in case of configuration error
     */
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session
                        -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/auth/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**")
                .permitAll()
                .anyRequest()
                .authenticated());

        return http.build();
    }

    /**
     * Registers BCrypt password encoder bean for cryptographic one-way password
     * hashing.
     *
     * @return {@link PasswordEncoder} instance
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the standard Spring Security AuthenticationManager bean.
     *
     * @param configuration authentication configuration
     * @return {@link AuthenticationManager}
     * @throws Exception in case of retrieval error
     */
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}
