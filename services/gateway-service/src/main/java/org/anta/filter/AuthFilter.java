package org.anta.filter;

import org.anta.config.JwtUtil;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private final List<String> openPrefixes = List.of(
            "/api/auth",
            "/api/product",
            "/api/public",
            "/api/products",
            "/api/cloud",
            "/actuator"
    );

    public AuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // if any prefix matches (startsWith) -> skip auth
        for (String p : openPrefixes) {
            if (path.startsWith(p)) {
                return chain.filter(exchange);
            }
        }

        // then normal JWT check
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        var mutated = exchange.getRequest().mutate()
                .header("X-User-Name", username == null ? "" : username)
                .header("X-User-Role", role == null ? "" : role)
                .build();

        ServerWebExchange newExchange = exchange.mutate().request(mutated).build();
        return chain.filter(newExchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}