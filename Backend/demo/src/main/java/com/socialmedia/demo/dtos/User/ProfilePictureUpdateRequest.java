package com.socialmedia.demo.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePictureUpdateRequest {
    private byte[] imageData;
    private String imageType;
    private boolean isAvatar; // true for avatar, false for cover picture
}