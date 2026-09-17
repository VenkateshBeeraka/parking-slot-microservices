package com.parking.gateway.filter;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CorsResponseHeaderFilter implements GlobalFilter, Ordered {

	@Override
	public int getOrder() {
		// Run with highest precedence so beforeCommit is registered early
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		exchange.getResponse().beforeCommit(() -> {
			HttpHeaders headers = exchange.getResponse().getHeaders();

			// 1. Deduplicate Access-Control-Allow-Origin
			List<String> origins = headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
			if (origins != null && origins.size() > 1) {
				// If multiple origins exist (e.g. '*' and 'http://localhost:3000'),
				// retain the specific client origin over wildcard '*'
				String originToKeep = origins.stream()
						.filter(o -> !o.equals("*"))
						.findFirst()
						.orElse(origins.get(0));
				headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, originToKeep);
			}

			// 2. Deduplicate Access-Control-Allow-Credentials
			List<String> credentials = headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
			if (credentials != null && credentials.size() > 1) {
				headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, credentials.get(0));
			}

			return Mono.empty();
		});

		return chain.filter(exchange);
	}
}
