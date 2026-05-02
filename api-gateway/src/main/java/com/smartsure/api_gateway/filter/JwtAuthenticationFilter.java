package com.smartsure.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.security.Key;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            // PUBLIC ROUTES
            if (path.startsWith("/api/auth")) {
                return chain.filter(exchange);
            }

            // CHECK AUTH HEADER
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);
            String queryToken = exchange.getRequest()
                    .getQueryParams()
                    .getFirst("access_token");

            if ((authHeader == null || authHeader.isBlank()) && (queryToken == null || queryToken.isBlank())) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
             //Bearer Token Check
            if ((authHeader == null || !authHeader.startsWith("Bearer ")) && (queryToken == null || queryToken.isBlank())) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }


              //Extract Token
            String token = authHeader != null && authHeader.startsWith("Bearer ")
                    ? authHeader.substring(7)
                    : queryToken;

            try {

                // Validate Token + Extract Data
                Claims claims = extractClaims(token);

                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                //Add Headers
                ServerHttpRequest modifiedRequest = exchange.getRequest()
                        .mutate()
                        .header("X-Auth-User", email)
                        .header("X-Auth-Role", role)
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                        .build();
                //Forward Request
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }
}
