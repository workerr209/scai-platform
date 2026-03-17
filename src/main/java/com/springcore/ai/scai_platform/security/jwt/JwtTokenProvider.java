package com.springcore.ai.scai_platform.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Data
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration-in-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-expiration-in-ms}")
    private long refreshTokenExpirationMs;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String email) {
        return buildToken(email, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String email) {
        return buildToken(email, refreshTokenExpirationMs);
    }

    private String buildToken(String subject, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return false;
        } catch (ExpiredJwtException ex) {
            System.out.println("JWT expired: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT format: " + ex.getMessage());
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT signature: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("JWT validation error: " + ex.getMessage());
        }
        return true;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser() // เปลี่ยนจาก parserBuilder() เป็น parser()
                .verifyWith(signingKey) // เปลี่ยนจาก setSigningKey() เป็น verifyWith()
                .build()
                .parseSignedClaims(token) // เปลี่ยนจาก parseClaimsJws() เป็น parseSignedClaims()
                .getPayload(); // เปลี่ยนจาก getBody() เป็น getPayload()
    }

}
