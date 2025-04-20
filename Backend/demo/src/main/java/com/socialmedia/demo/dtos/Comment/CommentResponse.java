package com.socialmedia.demo.dtos.Comment;

import com.socialmedia.demo.dtos.User.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private String id;
    private UserDTO author;
    private String postId;
    private String content;
    private byte[] imageData;
    private String imageType;
    private LocalDateTime createAt;
    private int reactionCount;
    private boolean hasUserReacted;
    private String userReactionType;
}