package com.socialmedia.demo.dtos.requests;

import com.socialmedia.demo.enums.PrivacySetting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String bio;
    private String gender;
    private Date dob;
    private PrivacySetting privacySetting;
}