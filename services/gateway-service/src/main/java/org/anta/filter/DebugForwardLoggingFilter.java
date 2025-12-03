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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DebugForwardLoggingFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(DebugForwardLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        Object forwardedObj = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String path = exchange.getRequest().getURI().getPath();

        String willForwardTo;
        if (forwardedObj == null) {
            willForwardTo = "null";
        } else if (forwardedObj instanceof List) {
            willForwardTo = ((List<?>) forwardedObj).toString();
        } else {
            willForwardTo = forwardedObj.toString();
        }

        log.info("[GATEWAY DEBUG] incoming={}, willForwardTo={}", path, willForwardTo);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // đặt sau nhiều filter khác để chắc chắn route đã resolved
        return Ordered.LOWEST_PRECEDENCE;
    }
}