package com.socialmedia.demo.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private String id;
    private String content;
    private byte[] imageData;
    private String imageType;
    private LocalDateTime createAt;
    private UserResponse author;
    private String postId;
}