package com.socialmedia.demo.dtos.Post;

import com.socialmedia.demo.dtos.User.UserDTO;
import com.socialmedia.demo.enums.PrivacySetting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private String id;
    private UserDTO author;
    private String content;
    private byte[] imageData;
    private String imageType;
    private LocalDateTime createAt;
    private PrivacySetting privacySetting;
    private int commentCount;
    private int reactionCount;
}