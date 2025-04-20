package com.socialmedia.demo.dtos.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentImageUpdateRequest {
    private byte[] imageData;
    private String imageType;
}