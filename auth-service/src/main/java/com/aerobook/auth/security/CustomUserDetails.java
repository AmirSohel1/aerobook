package com.aerobook.auth.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.aerobook.auth.entity.Credential;

/**
 * ============================================================================
 * Spring Security UserDetails Adapter
 * ============================================================================
 *
 * Adapts {@link Credential} entity into Spring Security's standard
 * {@link UserDetails} contract for credential verification and role authority
 * extraction.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
public class CustomUserDetails implements UserDetails {

    private final Credential credential;

    /**
     * Constructs adapter with underlying database credential.
     *
     * @param credential database entity
     */
    public CustomUserDetails(Credential credential) {
        this.credential = credential;
    }

    /**
     * Maps the credential's role into Spring Security GrantedAuthority
     * collection.
     *
     * @return collection of granted authorities (e.g. ROLE_USER, ROLE_ADMIN)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        credential.getRole().name()));
    }

    @Override
    public String getPassword() {
        return credential.getPassword();
    }

    @Override
    public String getUsername() {
        return credential.getEmail();
    }
}
