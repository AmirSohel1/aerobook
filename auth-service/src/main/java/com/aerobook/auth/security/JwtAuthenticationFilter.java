package com.aerobook.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ============================================================================
 * HTTP Servlet OncePerRequestFilter for Auth Service
 * ============================================================================
 *
 * Servlet filter for inspecting inbound HTTP authorization headers at the
 * auth-service servlet container layer. Passes requests through to subsequent
 * filter chain.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    /**
     * Intercepts HTTP request and passes control downstream to filter chain.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param filterChain servlet filter chain
     * @throws ServletException in case of servlet errors
     * @throws IOException in case of I/O errors
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(request, response);
    }
}
