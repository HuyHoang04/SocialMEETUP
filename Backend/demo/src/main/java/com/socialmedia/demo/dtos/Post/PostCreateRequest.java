package com.socialmedia.demo.dtos.Post;

import com.socialmedia.demo.enums.PrivacySetting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {
    private String content;
    private byte[] imageData;
    private String imageType;
    private PrivacySetting privacySetting;
}