package com.socialmedia.demo.dtos.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
    private String postId;
    private String content;
    private byte[] imageData;
    private String imageType;
}