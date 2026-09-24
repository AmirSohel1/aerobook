package com.aerobook.auth.entity;

/**
 * ============================================================================
 * Aerobook Security Role Enumeration
 * ============================================================================
 *
 * Defines the standard platform security roles for Role-Based Access Control
 * (RBAC):
 * <ul>
 * <li>{@link #ROLE_USER}: Standard authenticated customer with booking and
 * profile access.</li>
 * <li>{@link #ROLE_ADMIN}: Platform administrator with flight scheduling, fleet
 * management, fare pricing, and airline-wide oversight privileges.</li>
 * </ul>
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
public enum Role {

    /**
     * Standard passenger/customer security authority.
     */
    ROLE_USER,
    /**
     * Airport operations and flight crew staff authority.
     */
    ROLE_STAFF,
    /**
     * Administrator security authority with platform-wide write privileges.
     */
    ROLE_ADMIN
}
