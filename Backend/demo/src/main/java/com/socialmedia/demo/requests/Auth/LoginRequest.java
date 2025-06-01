package com.socialmedia.demo.requests.Auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String usernameOrEmail; // Cho phép đăng nhập bằng username hoặc email
    private String password;
}