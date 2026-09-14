package com.aerobook.user;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.aerobook.user.controller.UserController;
import com.aerobook.user.dto.request.UserRequest;
import com.aerobook.user.dto.response.UserResponse;
import com.aerobook.user.exception.GlobalExceptionHandler;
import com.aerobook.user.exception.ResourceNotFoundException;
import com.aerobook.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller unit tests for UserController validating endpoint routing, HTTP
 * status codes, and exception handler mappings.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleResponse = new UserResponse(
                1L,
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
    @DisplayName("GET /api/v1/users/health - should return 200 OK")
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Service is up and running!"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - should return 200 and user JSON when found")
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("Ravi"))
                .andExpect(jsonPath("$.email").value("ravi.patel@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - should return 404 NOT FOUND when user does not exist")
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User not found with ID: 999"));

        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found with ID: 999"));
    }

    @Test
    @DisplayName("GET /api/v1/users/email/{email} - should return 200 and user JSON")
    void testGetUserByEmail_Success() throws Exception {
        when(userService.getUserByEmail("ravi.patel@example.com")).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/users/email/ravi.patel@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ravi.patel@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/users - should return 200 OK for admin role")
    void testGetAllUsers_AdminSuccess() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/users")
                .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ravi.patel@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/users - should return 403 Forbidden for non-admin role header")
    void testGetAllUsers_ForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .header("X-User-Role", "ROLE_USER"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    @DisplayName("POST /api/v1/users - should return 201 Created on valid payload")
    void testCreateUser_Success() throws Exception {
        when(userService.createUser(any(UserRequest.class))).thenReturn(sampleResponse);

        String jsonPayload = """
                {
                  "firstName": "Ravi",
                  "lastName": "Patel",
                  "email": "ravi.patel@example.com",
                  "phoneNumber": "9876543212",
                  "dateOfBirth": "1995-09-20",
                  "nationality": "Indian",
                  "role": "ROLE_USER"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("Ravi"));
    }

    @Test
    @DisplayName("POST /api/v1/users - should return 400 Bad Request on blank required fields")
    void testCreateUser_ValidationError() throws Exception {
        String invalidPayload = """
                {
                  "firstName": "",
                  "lastName": "",
                  "email": "not-an-email",
                  "phoneNumber": ""
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isMap());
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} - should return 200 OK on update")
    void testUpdateUser_Success() throws Exception {
        when(userService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(sampleResponse);

        String updatePayload = """
                {
                  "firstName": "Ravi",
                  "lastName": "Patel",
                  "email": "ravi.patel@example.com",
                  "phoneNumber": "9876543212",
                  "dateOfBirth": "1995-09-20",
                  "nationality": "Indian",
                  "role": "ROLE_USER"
                }
                """;

        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - should return 200 OK for admin role")
    void testDeleteUser_AdminSuccess() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1")
                .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string("User account deleted successfully with ID: 1"));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - should return 403 Forbidden for non-admin role header")
    void testDeleteUser_ForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1")
                .header("X-User-Role", "ROLE_USER"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: Deleting a user requires ROLE_ADMIN authority."));
    }
}
