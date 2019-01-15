package jwtTest.controllers;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jwtTest.components.JwtTokenProvider;
import reactor.core.publisher.Mono;

@org.springframework.web.bind.annotation.RestController
public class RestController {
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@RequestMapping("rest/")
	public Mono<ResponseEntity<String>> generateToken(@RequestParam(value = "token", required = false) String tokenString) {
		return Mono.just(ResponseEntity.ok(jwtTokenProvider.createToken(Objects.nonNull(tokenString) ? tokenString : "hello")));
	}

	@RequestMapping("rest/validate")
	public boolean validateToken(ServerHttpRequest req) {
		String tokenString = jwtTokenProvider.resolveToken(req);
		return jwtTokenProvider.validateToken(tokenString);
	}

	@RequestMapping("rest/getJwtDetails")
	public Jws<Claims> getJwtDetails(@RequestParam(value = "token") String token) {
		return jwtTokenProvider.getJws(token);
	}
}
