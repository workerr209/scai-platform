package com.springcore.ai.scaiplatform.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    // Not exposed — raw secret must never be readable outside this class
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Not exposed — callers generate tokens via generateAccessToken(); they don't need the raw value
    @Value("${jwt.access-expiration-in-ms}")
    private long accessTokenExpirationMs;

    // Exposed so callers (e.g. AuthServiceImpl) can include expiry in the auth response
    @Getter
    @Value("${jwt.refresh-expiration-in-ms}")
    private long refreshTokenExpirationMs;

    // Derived from jwtSecret after @PostConstruct; never exposed via getter
    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        // Decode the Base64-encoded secret string into raw bytes before building the key.
        // Using getBytes(UTF_8) directly on a Base64 string produces a weak and incorrect key.
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
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

    /**
     * Validates the given JWT token.
     *
     * @return true if the token is valid and not expired; false otherwise
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            // Parsing succeeded — token is structurally valid and not expired
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("JWT expired: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("Invalid JWT format: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.warn("Invalid JWT signature: {}", ex.getMessage());
        } catch (Exception ex) {
            log.warn("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
