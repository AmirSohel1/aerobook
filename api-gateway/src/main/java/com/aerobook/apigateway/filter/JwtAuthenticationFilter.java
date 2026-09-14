package com.aerobook.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * ============================================================================
 * Global Request Auditing and Diagnostic Filter
 * ============================================================================
 *
 * Reactive {@link GlobalFilter} executed on every HTTP request entering the API
 * Gateway. Configured with high precedence ({@code getOrder() == -1}) to run
 * before route-specific filters.
 *
 * <p>
 * Key Responsibilities:
 * <ul>
 * <li>Logs target request URI, path, and method for distributed tracing.</li>
 * <li>Acts as an early diagnostic hook for ingress network inspection.</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log
            = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * Intercepts the reactive exchange and logs the request URI before passing
     * execution to the next filter in the chain.
     *
     * @param exchange current server web exchange containing request and
     * response
     * @param chain the gateway filter chain
     * @return {@link Mono<Void>} completing when the downstream filters
     * complete
     */
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        log.info("Incoming Gateway Request: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI());

        return chain.filter(exchange);
    }

    /**
     * Defines the execution priority in the global filter chain. Order
     * {@code -1} ensures execution prior to standard gateway routing filters.
     *
     * @return execution order integer
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
