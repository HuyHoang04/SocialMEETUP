package com.socialmedia.demo.controllers;

import com.socialmedia.demo.requests.User.UserCreateRequest;
import com.socialmedia.demo.requests.User.UserUpdateRequest;
import com.socialmedia.demo.responses.ApiResponse;
import com.socialmedia.demo.responses.UserResponse;
import com.socialmedia.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users") // Base path cho tất cả các endpoint trong controller này
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Endpoint để tạo người dùng mới
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserCreateRequest request) {
        UserResponse createdUser = userService.createUser(request);
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.CREATED.value())); // 201
        response.setResult(createdUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint để lấy thông tin người dùng bằng ID
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String userId) {
        UserResponse user = userService.getUserById(userId);
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(user);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy thông tin người dùng bằng email
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserByEmail(email);
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(user);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy thông tin người dùng bằng username
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        UserResponse user = userService.getUserByUsername(username);
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(user);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy danh sách tất cả người dùng
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        ApiResponse<List<UserResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(users);
        return ResponseEntity.ok(response);
    }

    // Endpoint để cập nhật thông tin người dùng
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        UserResponse updatedUser = userService.updateUser(userId, request);
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(updatedUser);
        return ResponseEntity.ok(response);
    }

    // Endpoint để xóa người dùng
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // Hoặc NO_CONTENT (204) tùy theo thiết kế API
        response.setResult("User deleted successfully with id: " + userId);
        return ResponseEntity.ok(response); // Hoặc ResponseEntity.noContent().build();
    }
}