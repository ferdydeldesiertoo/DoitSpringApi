package org.api.doit.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Service responsible for generating, validating, and extracting information from JWT tokens.
 */
@Service
public class JwtService {
    @Value("${app.jwt.secret}")
    private String SECRET;

    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    /**
     * Returns the secret key used for signing and verifying the JWT.
     *
     * @return a SecretKey for HMAC-SHA algorithm.
     */
    private SecretKey getSignInKey() {
        // Converts the configured string secret into a SecretKey object
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Generates a JWT token for the provided username.
     *
     * @param username the username to include in the token.
     * @param id the user id to include in the token.
     * @return a signed JWT token.
     */
    public String generateToken(String username, UUID id) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", id) // Adds a custom claim with the user's UUID
                .issuedAt(new Date()) // Sets the token issuance time to current time
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Sets expiration time
                .signWith(getSignInKey(), Jwts.SIG.HS256) // Signs the token with HMAC SHA-256
                .compact(); // Builds and returns the compact JWT string
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param token the JWT token.
     * @param claimName the name of the claim to extract.
     * @param clazz the class type of the expected claim value.
     * @return the value of the claim cast to the specified class.
     */
    public <T> T extractClaim(String token, String claimName, Class<T> clazz) {
        return Jwts.parser()
                .verifyWith(getSignInKey()) // Uses the secret key to verify token's signature
                .build()
                .parseSignedClaims(token) // Parses and verifies the token
                .getPayload()
                .get(claimName, clazz); // Retrieves the specific claim
    }

    /**
     * Validates the token by checking its expiration.
     *
     * @param token the JWT token.
     * @return true if the token is still valid.
     */
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    /**
     * Checks if the token has expired based on its "exp" claim.
     *
     * @param token the JWT token.
     * @return true if the token is expired.
     */
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration(); // Extracts expiration date from token

        return expiration.before(new Date()); // Compares expiration with current time
    }
}
