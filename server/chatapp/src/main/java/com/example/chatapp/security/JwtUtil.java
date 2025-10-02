package com.example.chatapp.security;

import com.example.chatapp.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expiration = 1000 * 60 * 60; // 1 hour

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())              // <-- user ID here
                .claim("uname", user.getUsername())               // optional
                .claim("roles", user.getRole())                  // e.g. ["USER"]
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractId(String token) {
        return Long.valueOf(getClaims(token).getSubject()); // "1"
    }

    public String extractUsername(String token) {
        return getClaims(token).get("uname", String.class); // "Willem"
    }

    public java.util.List<String> extractRoles(String token) {
        Object v = getClaims(token).get("roles");
        if (v == null) return java.util.List.of();
        if (v instanceof java.util.List<?> list)
            return list.stream().map(String::valueOf).toList();
        return java.util.List.of(String.valueOf(v)); // handle single string "USER"
    }

    public boolean isTokenValid(String token) {
        try {
            var c = getClaims(token);
            var exp = c.getExpiration();
            return exp != null && exp.after(new java.util.Date());
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
