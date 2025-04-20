package com.socialmedia.demo.dtos.Post;

import com.socialmedia.demo.dtos.Comment.CommentDTO;
import com.socialmedia.demo.dtos.User.UserDTO;
import com.socialmedia.demo.enums.PrivacySetting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private String id;
    private UserDTO author;
    private String content;
    private byte[] imageData;
    private String imageType;
    private LocalDateTime createAt;
    private PrivacySetting privacySetting;
    private List<CommentDTO> comments;
    private int commentCount;
    private int reactionCount;
    private boolean hasUserReacted;
    private String userReactionType;
}