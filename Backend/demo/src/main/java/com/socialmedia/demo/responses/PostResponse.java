package com.socialmedia.demo.responses;

import com.socialmedia.demo.enums.PrivacySetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private String id;
    private String content;
    private byte[] imageData;
    private String imageType;
    private LocalDateTime createAt;
    private PrivacySetting privacySetting;
    private UserResponse author;
}