package jwtTest.components.filters;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class RequestFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		// TODO Auto-generated method stub
		String traceId = UUID.randomUUID().toString();
		long startTime = System.currentTimeMillis();
		String path = exchange.getRequest().getURI().getPath();
		System.out.printf("Request[%s] started, trace_id[%s]", path, traceId);
		System.out.println();
		return chain.filter(exchange).doAfterSuccessOrError((r, t) -> {
			System.out.printf("Request[%s], completed, status_code[%s], time[%d], trace_id[%s]", path,
					exchange.getResponse().getStatusCode(), System.currentTimeMillis() - startTime, traceId);
		});
	}

}
