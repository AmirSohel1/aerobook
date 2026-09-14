package com.aerobook.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aerobook.auth.entity.Credential;
import com.aerobook.auth.repository.CredentialRepository;

/**
 * ============================================================================
 * Spring Security UserDetailsService Implementation
 * ============================================================================
 *
 * Loads user credentials by registered email address from MySQL database for
 * Spring Security authentication operations.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CredentialRepository credentialRepository;

    /**
     * Constructs service with required credential repository.
     *
     * @param credentialRepository data access repository
     */
    public CustomUserDetailsService(
            CredentialRepository credentialRepository) {

        this.credentialRepository = credentialRepository;
    }

    /**
     * Loads user credentials by email username.
     *
     * @param email registered account email
     * @return {@link UserDetails} adapter wrapping credential record
     * @throws UsernameNotFoundException if email is not registered in system
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Credential credential = credentialRepository
                .findByEmail(email)
                .orElseThrow(()
                        -> new UsernameNotFoundException(
                        "User not found"));

        return new CustomUserDetails(credential);
    }
}
