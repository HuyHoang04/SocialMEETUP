package com.socialmedia.demo.dtos.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {
    private String id;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}