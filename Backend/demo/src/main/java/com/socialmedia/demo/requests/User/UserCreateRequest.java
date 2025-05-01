package com.socialmedia.demo.requests.User;

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
public class UserCreateRequest {
    private String email;
    private String username;
    private String fullname;
    private String password;
    private String bio;
    private String gender;
    private Date dob;
    private PrivacySetting privacySetting;
    private byte[] avatar;
    private String avatarType;
    private byte[] coverPicture;
    private String coverPictureType;
    private ROLE role;
}