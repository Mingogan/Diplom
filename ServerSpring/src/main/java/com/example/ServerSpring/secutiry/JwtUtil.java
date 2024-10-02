package com.example.ServerSpring.secutiry;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.refreshSecret}")
    private String refreshSecret;

    public String getSecretKey() {
        //System.out.println("Secret Key: " + secretKey);
        return secretKey;
    }

    public String getRefreshSecret() {
        //System.out.println("Refresh Secret Key: " + refreshSecret);
        return refreshSecret;
    }

    public String generateToken(String username) {
        //System.out.println("Generating token with secret key: " + secretKey);
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, secretKey, 1000  * 15); // 15 минут
    }

    public String generateRefreshToken(String username) {
        System.out.println("Generating refresh token with refresh secret key: " + refreshSecret);
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, refreshSecret, 1000 * 60 * 60 * 24); // 24 часа
    }

    private String createToken(Map<String, Object> claims, String subject, String secret, long expiration) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean validateToken(String token, String secret) {
        final String username = extractUsername(token, secret);
        return (username != null && !isTokenExpired(token, secret));
    }

    private Claims extractAllClaims(String token, String secret) {
        //System.out.println("Extracting claims from token: " + token);
        //System.out.println("Using secret key: " + secret);
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token, String secret) {
        //System.out.println("Extracting username from token: " + token);
        return extractClaim(token, Claims::getSubject, secret);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secret) {
        final Claims claims = extractAllClaims(token, secret);
        return claimsResolver.apply(claims);
    }

    private Boolean isTokenExpired(String token, String secret) {
        return extractExpiration(token, secret).before(new Date());
    }

    private Date extractExpiration(String token, String secret) {
        return extractClaim(token, Claims::getExpiration, secret);
    }
}