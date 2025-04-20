package com.socialmedia.demo.dtos.Post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostImageUpdateRequest {
    private byte[] imageData;
    private String imageType;
}