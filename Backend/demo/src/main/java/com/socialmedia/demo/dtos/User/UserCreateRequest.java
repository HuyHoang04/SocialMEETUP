package com.socialmedia.demo.dtos.User;

import com.socialmedia.demo.enums.PrivacySetting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    private String email;
    private String username;
    private String password;
    private String bio;
    private String gender;
    private Date dob;
    private PrivacySetting privacySetting;
}