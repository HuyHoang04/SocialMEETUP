package com.socialmedia.demo.dtos.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePictureUpdateRequest {
    private String id;
    private byte[] imageData;
    private String imageType;
    private boolean isAvatar; // true for avatar, false for cover picture
}