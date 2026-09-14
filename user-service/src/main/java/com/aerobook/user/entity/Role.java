package com.aerobook.user.entity;

/**
 * ============================================================================
 * User Security Role Enumeration
 * ============================================================================
 *
 * Defines the standard security authorities assigned to user profiles in the
 * Aerobook platform.
 *
 * <ul>
 * <li>{@link #ROLE_USER}: Standard customer authority allowed to view/edit own
 * profile</li>
 * <li>{@link #ROLE_ADMIN}: Administrative authority with full system directory
 * access</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public enum Role {

    /**
     * Standard passenger/customer role with self-service profile capabilities.
     */
    ROLE_USER,
    /**
     * System administrator role with full user management privileges.
     */
    ROLE_ADMIN
}
