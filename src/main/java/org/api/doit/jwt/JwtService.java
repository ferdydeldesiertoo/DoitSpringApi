package org.api.doit.jwt;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Service responsible for generating, validating, and extracting information from JWT tokens.
 */
@Service
public class JwtService {

    private final String SECRET = "secret123teamosadehdthtdhtdhhdthdsregfergrghdthdthdthdt";
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    /**
     * Returns the secret key used for signing and verifying the JWT.
     * @return a SecretKey for HMAC-SHA algorithm.
     */
    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Generates a JWT token for the provided username.
     * @param username the username to include in the token.
     * @param id the user id to include in the token.
     * @return a signed JWT token.
     */
    public String generateToken(String username, UUID id) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", id)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts the username from a given JWT token.
     * @param token the JWT token.
     * @return the username contained in the token.
     */
//    public String extractUsername(String token) {
//        return Jwts.parser()
//                .verifyWith(getSignInKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();
//    }

    public <T> T extractClaim(String token, String claimName, Class<T> clazz) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(claimName, clazz);
    }


    /**
     * Validates the token for the specified username.
     * @param token the JWT token.
     * @return true if the token is no expired.
     */
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    /**
     * Checks if the token has expired.
     * @param token the JWT token.
     * @return true if the token is expired.
     */
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expiration.before(new Date());
    }
}

