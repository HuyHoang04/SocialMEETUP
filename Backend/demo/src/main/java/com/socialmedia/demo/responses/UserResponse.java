package com.socialmedia.demo.responses;

import com.socialmedia.demo.enums.PrivacySetting;
import com.socialmedia.demo.enums.ROLE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String username;
    private String fullname;
    private String bio;
    private String gender;
    private Date dob;
    private Date createdAt;
    private PrivacySetting privacySetting;
    private byte[] avatar;
    private String avatarType;
    private byte[] coverPicture;
    private String coverPictureType;
    private ROLE role;
}