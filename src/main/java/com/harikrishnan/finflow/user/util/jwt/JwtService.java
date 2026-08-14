package com.harikrishnan.finflow.user.util.jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class JwtService {

    public final JwtConfiguration jwtConfiguration;

    private SecretKey getSecretKey () {
        return Keys.hmacShaKeyFor(jwtConfiguration.getSecret().getBytes());
    }

    private String generateToken (String subject, Long expirationTime, Map<String,String > claims) {
        return Jwts.builder()
                .signWith(getSecretKey())
                .subject(subject)
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .claims(claims)
                .compact();
    }

    private Jws<Claims> getClaims (String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
    }


    public String generateAccessToken (String emailId) {
        return generateToken(emailId, jwtConfiguration.getAccessTokenExpiration(), Map.of());
    }

    public String extractEmailIdFromToken (String token) {
        return getClaims(token).getPayload().getSubject();
    }

    public Date extractExpirationDate (String token) {
        return getClaims(token).getPayload().getExpiration();
    }

    public boolean isTokenValid (String token, String emailId) {
        return extractEmailIdFromToken(token).equals(emailId) && extractExpirationDate(token).after(new Date());
    }


}
