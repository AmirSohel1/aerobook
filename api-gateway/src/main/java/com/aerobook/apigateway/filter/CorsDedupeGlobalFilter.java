package com.aerobook.apigateway.filter;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Global filter that ensures CORS headers (Access-Control-Allow-Origin,
 * Access-Control-Allow-Methods, Access-Control-Allow-Headers, etc.) are never
 * duplicated across downstream microservices and the gateway.
 */
@Component
public class CorsDedupeGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getResponse().beforeCommit(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            dedupeHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
            dedupeHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
            dedupeHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
            dedupeHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
            dedupeHeader(headers, HttpHeaders.VARY);
            return Mono.empty();
        });
        return chain.filter(exchange);
    }

    private void dedupeHeader(HttpHeaders headers, String headerName) {
        List<String> values = headers.get(headerName);
        if (values != null && !values.isEmpty()) {
            String firstVal = values.get(0);
            if (firstVal != null && firstVal.contains(",")) {
                String[] parts = firstVal.split(",");
                firstVal = parts[0].trim();
            }
            if (values.size() > 1 || !firstVal.equals(values.get(0))) {
                headers.set(headerName, firstVal);
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
