package com.url.shortener.gateway.security.jwt;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private JwtUtils jwtUtils;

    public JwtAuthenticationFilter() {
        super();
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if(request.getURI().getPath().startsWith("/auth")) {
                return chain.filter(exchange);
            }
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization header.");
            }

            String jwtToken = jwtUtils.getJwtFromHeader(request);
            if (!jwtUtils.validateToken(jwtToken)) {
                return onError(exchange, "Invalid JWT Token.");
            }

            String username = jwtUtils.getUsernameFromJwtToken(jwtToken);
            exchange.getRequest().mutate().header("X-User", username).build();
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError (ServerWebExchange exchange, String err){
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {

    }
}
