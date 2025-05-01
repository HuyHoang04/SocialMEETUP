package com.socialmedia.demo.requests.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
    private String authorId;
    private String postId;
    private String content;
    private byte[] imageData;
    private String imageType;
}