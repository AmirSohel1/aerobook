package com.aerobook.auth;

import com.aerobook.auth.entity.Credential;
import com.aerobook.auth.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test suite verifying core domain entity instantiation and field
 * mappings.
 *
 * @author AeroBook Development Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Application Domain Tests")
class AuthServiceApplicationTests {

    /**
     * Verifies that the {@link Credential} entity can be instantiated,
     * populated, and queried without errors.
     */
    @Test
    @DisplayName("Should successfully instantiate and verify Credential entity fields")
    void credentialEntityInstantiatesCorrectly() {
        Credential credential = new Credential();
        credential.setCredentialId(1L);
        credential.setUserId(10L);
        credential.setEmail("asha.khan@example.com");
        credential.setPassword("hashedPassword");
        credential.setRole(Role.ROLE_USER);
        credential.setCreatedAt(LocalDateTime.now());
        credential.setUpdatedAt(LocalDateTime.now());

        assertThat(credential.getCredentialId()).isEqualTo(1L);
        assertThat(credential.getUserId()).isEqualTo(10L);
        assertThat(credential.getEmail()).isEqualTo("asha.khan@example.com");
        assertThat(credential.getRole()).isEqualTo(Role.ROLE_USER);
    }
}
