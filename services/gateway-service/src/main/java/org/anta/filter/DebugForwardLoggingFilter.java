package org.anta.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;

import java.net.URI;
import java.util.List;

@Component
public class DebugForwardLoggingFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(DebugForwardLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        List<URI> forwarded = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String path = exchange.getRequest().getURI().getPath();
        log.info("[GATEWAY DEBUG] incoming={}, willForwardTo={}", path, (forwarded==null? "null": forwarded));
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -50;
    }
}
