package com.aerobook.auth.util;

/**
 * Application-wide security and token constant definitions for auth-service.
 *
 * <p>
 * Contains header keys, token schemas, default expiration limits, and role
 * definitions.</p>
 *
 * @author AeroBook Development Team
 * @version 1.0
 */
public final class Constants {

    /**
     * Private constructor to prevent instantiation of utility constant class.
     */
    private Constants() {
    }

    /**
     * Token type prefix returned in authorization response DTOs (e.g.
     * "Bearer").
     */
    public static final String TOKEN_TYPE = "Bearer";

    /**
     * Default security authority role assigned to registered customer accounts.
     */
    public static final String ROLE_USER = "ROLE_USER";

    /**
     * Elevated security authority role granting access to administrative
     * operations.
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /**
     * Default JWT access token expiration duration (24 hours in milliseconds).
     */
    public static final long ACCESS_TOKEN_EXPIRATION
            = 24 * 60 * 60 * 1000;

    /**
     * Default refresh token lifetime duration (7 days in milliseconds).
     */
    public static final long REFRESH_TOKEN_EXPIRATION
            = 7 * 24 * 60 * 60 * 1000;

    /**
     * HTTP header key used to convey bearer authorization tokens.
     */
    public static final String AUTH_HEADER
            = "Authorization";

    /**
     * Prefix preceding JWT token in HTTP Authorization header value.
     */
    public static final String TOKEN_PREFIX
            = "Bearer ";
}
