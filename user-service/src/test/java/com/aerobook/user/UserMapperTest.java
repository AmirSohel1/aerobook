package com.aerobook.user;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.aerobook.user.dto.request.UserRequest;
import com.aerobook.user.dto.response.UserResponse;
import com.aerobook.user.entity.Role;
import com.aerobook.user.entity.UserEntity;
import com.aerobook.user.mapper.UserMapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserMapper verifying mapping accuracy and null-safety.
 */
class UserMapperTest {

    @Test
    @DisplayName("toEntity - should map all fields from UserRequest to UserEntity")
    void testToEntity_Success() {
        UserRequest request = new UserRequest(
                "Ravi",
                "Patel",
                "ravi.patel@example.com",
                "9876543212",
                LocalDate.of(1995, 9, 20),
                "Indian",
                "ROLE_ADMIN"
        );

        UserEntity entity = UserMapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getFirstName()).isEqualTo("Ravi");
        assertThat(entity.getLastName()).isEqualTo("Patel");
        assertThat(entity.getEmail()).isEqualTo("ravi.patel@example.com");
        assertThat(entity.getPhoneNumber()).isEqualTo("9876543212");
        assertThat(entity.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 9, 20));
        assertThat(entity.getNationality()).isEqualTo("Indian");
        assertThat(entity.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("toEntity - should fallback to ROLE_USER when role is null or invalid")
    void testToEntity_RoleFallback() {
        UserRequest requestWithNullRole = new UserRequest(
                "Ravi", "Patel", "ravi@example.com", "9876543212",
                LocalDate.of(1995, 9, 20), "Indian", null
        );
        UserEntity entity1 = UserMapper.toEntity(requestWithNullRole);
        assertThat(entity1.getRole()).isEqualTo(Role.ROLE_USER);

        UserRequest requestWithInvalidRole = new UserRequest(
                "Ravi", "Patel", "ravi@example.com", "9876543212",
                LocalDate.of(1995, 9, 20), "Indian", "SUPER_USER"
        );
        UserEntity entity2 = UserMapper.toEntity(requestWithInvalidRole);
        assertThat(entity2.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    @DisplayName("toEntity - should return null when request is null")
    void testToEntity_NullRequest() {
        assertThat(UserMapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toResponse - should map all fields from UserEntity to UserResponse")
    void testToResponse_Success() {
        UserEntity entity = new UserEntity(
                1L,
                "Ravi",
                "Patel",
                "ravi.patel@example.com",
                "9876543212",
                LocalDate.of(1995, 9, 20),
                "Indian",
                Role.ROLE_USER
        );

        UserResponse response = UserMapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Ravi");
        assertThat(response.getLastName()).isEqualTo("Patel");
        assertThat(response.getEmail()).isEqualTo("ravi.patel@example.com");
        assertThat(response.getPhoneNumber()).isEqualTo("9876543212");
        assertThat(response.getNationality()).isEqualTo("Indian");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("toResponse - should return null when entity is null")
    void testToResponse_NullEntity() {
        assertThat(UserMapper.toResponse(null)).isNull();
    }
}
