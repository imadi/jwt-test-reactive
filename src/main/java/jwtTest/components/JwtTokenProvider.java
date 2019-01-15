package jwtTest.components;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenProvider {

	@Value("${security.jwt.token.secret-key:secret-key}")
	private String secretKey;

	@Value("${security.jwt.token.expire-length:3600000}")
	private long validityInMilliseconds = 3600000; // 1h

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String createToken(String tokenParams) {
		Claims claims = Jwts.claims().setSubject(tokenParams);
		claims.put("auth", "test");
		Date now = new Date();
		String token = Jwts.builder().compressWith(CompressionCodecs.DEFLATE).setClaims(claims).setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + validityInMilliseconds)).signWith(HS256, secretKey).compact();
		return token;
	}

	public String resolveToken(ServerHttpRequest req) {
		System.out.println(req.getHeaders().get("Authorization"));
		String bearerToken = req.getHeaders().get("Authorization").get(0);
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return bearerToken;
	}

	public boolean validateToken(String token) {
		try {
			getJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			System.out.println(e);
			return false;
		}
	}

	public Jws<Claims> getJws(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
	}

}
