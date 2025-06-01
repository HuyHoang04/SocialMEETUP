package com.socialmedia.demo.responses;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor // Tạo constructor cho final field
public class JwtAuthenticationResponse {
    private final String accessToken;
    private String tokenType = "Bearer"; // Loại token mặc định
}