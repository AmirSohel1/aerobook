// package com.aerobook.flight.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration reference for Flight Service.
 * Perimeter security, authentication, and JWT authorization are enforced upstream
 * by the API Gateway. Inter-service and local endpoint security can optionally be
 * configured here if standalone security is required.
 */
// @Configuration
// public class SecurityConfig {

//     @Bean
//     public SecurityFilterChain securityFilterChain(
//             HttpSecurity http) throws Exception {

//         http
//                 .csrf(csrf -> csrf.disable())
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers(
//                                 "/swagger-ui/**",
//                                 "/v3/api-docs/**")
//                         .permitAll()
//                         .anyRequest()
//                         .authenticated())
//                 .httpBasic(Customizer.withDefaults());

//         return http.build();
//     }
// }
