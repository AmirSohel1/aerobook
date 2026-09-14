package com.aerobook.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aerobook.user.dto.request.UserRequest;
import com.aerobook.user.dto.response.UserResponse;
import com.aerobook.user.entity.Role;
import com.aerobook.user.entity.UserEntity;
import com.aerobook.user.exception.ResourceNotFoundException;
import com.aerobook.user.repository.UserRepository;
import com.aerobook.user.service.impl.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Simple, fast unit tests for UserServiceImpl using Mockito. Tests business
 * logic, role defaults, duplicate checks, and exception scenarios.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity sampleEntity;
    private UserRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleEntity = new UserEntity(
                1L,
                "Ravi",
                "Patel",
                "ravi.patel@example.com",
                "9876543212",
                LocalDate.of(1995, 9, 20),
                "Indian",
                Role.ROLE_USER
        );

        sampleRequest = new UserRequest(
                "Ravi",
                "Patel",
                "ravi.patel@example.com",
                "9876543212",
                LocalDate.of(1995, 9, 20),
                "Indian",
                "ROLE_USER"
        );
    }

    @Test
    @DisplayName("createUser - should successfully save and return UserResponse")
    void testCreateUser_Success() {
        when(userRepository.existsByEmail("ravi.patel@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(sampleEntity);

        UserResponse response = userService.createUser(sampleRequest);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("ravi.patel@example.com");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("createUser - should throw IllegalArgumentException when email already exists")
    void testCreateUser_DuplicateEmail() {
        when(userRepository.existsByEmail("ravi.patel@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(sampleRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("getUserById - should return user when ID exists")
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        UserResponse response = userService.getUserById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Ravi");
    }

    @Test
    @DisplayName("getUserById - should throw ResourceNotFoundException when user not found")
    void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("getUserByEmail - should return user when email exists")
    void testGetUserByEmail_Success() {
        when(userRepository.findByEmail("ravi.patel@example.com")).thenReturn(Optional.of(sampleEntity));

        UserResponse response = userService.getUserByEmail("ravi.patel@example.com");

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("ravi.patel@example.com");
    }

    @Test
    @DisplayName("getAllUsers - should return list of all users")
    void testGetAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(sampleEntity));

        List<UserResponse> list = userService.getAllUsers();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getEmail()).isEqualTo("ravi.patel@example.com");
    }

    @Test
    @DisplayName("updateUser - should update fields and return updated response")
    void testUpdateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(sampleEntity);

        UserRequest updateReq = new UserRequest(
                "Ravi",
                "Sharma",
                "ravi.patel@example.com",
                "9876543212",
                LocalDate.of(1995, 9, 20),
                "Indian",
                "ROLE_ADMIN"
        );

        UserResponse response = userService.updateUser(1L, updateReq);

        assertThat(response).isNotNull();
        verify(userRepository).save(sampleEntity);
    }

    @Test
    @DisplayName("deleteUser - should delete user when ID exists")
    void testDeleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        userService.deleteUser(1L);

        verify(userRepository).delete(sampleEntity);
    }

    @Test
    @DisplayName("deleteUser - should throw ResourceNotFoundException when user not found")
    void testDeleteUser_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository, never()).delete(any(UserEntity.class));
    }
}
