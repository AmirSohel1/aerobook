package com.aerobook.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global Gateway filter that assigns and propagates a unique distributed
 * tracing correlation ID (X-Correlation-Id) across the Aerobook microservice
 * landscape.
 */
@Component
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdGlobalFilter.class);
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = "AB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        final String finalCorrelationId = correlationId;

        // Propagate header downstream and echo in response header
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CORRELATION_ID_HEADER, finalCorrelationId)
                .build();

        exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);

        log.debug("Assigned Correlation ID: {} to {} {}", finalCorrelationId, request.getMethod(), request.getURI().getPath());

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doOnEach(signal -> {
                    MDC.put("correlationId", finalCorrelationId);
                })
                .doFinally(signalType -> MDC.clear());
    }

    @Override
    public int getOrder() {
        // High precedence: run before authentication and routing filters
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
