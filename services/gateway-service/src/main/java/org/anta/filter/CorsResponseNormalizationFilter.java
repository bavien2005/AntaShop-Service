package org.anta.filter;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorsResponseNormalizationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            // remove any existing Access-Control-Allow-Origin to avoid duplicates
            headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
            // then add single allowed origin
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173");
            // ensure credentials header present if needed
            headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }));
    }

    @Override
    public int getOrder() {
        // Chạy sau các filter khác, trước khi response về client
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}
