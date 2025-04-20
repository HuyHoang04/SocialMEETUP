package com.socialmedia.demo.dtos.User;

import com.socialmedia.demo.enums.PrivacySetting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String email;
    private String username;
    private String bio;
    private String gender;
    private Date dob;
    private PrivacySetting privacySetting;
    private byte[] avatar;
    private String avatarType;
    private byte[] coverPicture;
    private String coverPictureType;
    private String role;
}