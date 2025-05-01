package com.socialmedia.demo.requests.Post;

import com.socialmedia.demo.enums.PrivacySetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {
    private String authorId;
    private String content;
    private byte[] imageData;
    private String imageType;
    private PrivacySetting privacySetting;
}