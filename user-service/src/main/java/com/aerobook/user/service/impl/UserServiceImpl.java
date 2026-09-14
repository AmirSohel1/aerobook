package com.aerobook.user.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerobook.user.dto.request.UserRequest;
import com.aerobook.user.dto.response.UserResponse;
import com.aerobook.user.entity.Role;
import com.aerobook.user.entity.UserEntity;
import com.aerobook.user.exception.ResourceNotFoundException;
import com.aerobook.user.mapper.UserMapper;
import com.aerobook.user.repository.UserRepository;
import com.aerobook.user.service.UserService;

/**
 * ============================================================================
 * User Profile Management Service Implementation
 * ============================================================================
 *
 * Implements transactional business logic for creating, updating, retrieving,
 * and deleting customer profile accounts in {@code aerobook_user_db}.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    /**
     * Constructor injection for required database repository.
     *
     * @param userRepository data access repository
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponse createUser(UserRequest request) {
        log.info("Creating customer profile for email: {}", request.getEmail());

        // 1. Verify email uniqueness prior to persistence
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            log.warn("Profile creation rejected: Email {} is already registered", request.getEmail());
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        // 2. Map request DTO into domain entity model
        UserEntity user = UserMapper.toEntity(request);

        // 3. Persist and return the mapped response projection
        UserResponse response = UserMapper.toResponse(userRepository.save(user));
        log.info("Successfully created user profile ID: {} for email: {}", response.getUserId(), response.getEmail());
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Retrieving user profile with ID: {}", id);
        // Locate entity by primary key or fail with 404
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return UserMapper.toResponse(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.debug("Retrieving user profile with email: {}", email);
        // Search by unique email or fail with 404
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserMapper.toResponse(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        // Stream all records and project to response DTOs
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponse updateUser(Long userId, UserRequest request) {
        log.info("Updating user profile for user ID: {}", userId);

        // 1. Retrieve existing entity or fail with 404
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // 2. Guard against duplicate email collisions if email is modified
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            log.warn("Profile update rejected: Email {} is already in use", request.getEmail());
            throw new IllegalArgumentException("Email " + request.getEmail() + " is already in use by another account");
        }

        // 3. Apply updated coordinates
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setNationality(request.getNationality());

        // 4. Validate and apply updated authority role if specified
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                user.setRole(Role.valueOf(request.getRole().trim().toUpperCase()));
            } catch (IllegalArgumentException exception) {
                log.warn("Invalid role update '{}' requested for user ID: {}", request.getRole(), userId);
                throw new IllegalArgumentException("Invalid user role: " + request.getRole() + ". Supported roles: ROLE_USER, ROLE_ADMIN");
            }
        }

        // 5. Save and return updated entity
        UserResponse response = UserMapper.toResponse(userRepository.save(user));
        log.info("Successfully updated user profile for user ID: {}", response.getUserId());
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user profile with user ID: {}", userId);
        // Locate entity by primary key or throw 404
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Delete from database
        userRepository.delete(user);
        log.info("Successfully deleted user profile with user ID: {}", userId);
    }
}
