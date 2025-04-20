package com.socialmedia.demo.dtos.PostReaction;

import com.socialmedia.demo.dtos.User.UserDTO;
import com.socialmedia.demo.dtos.Post.PostDTO;
import com.socialmedia.demo.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostReactionDTO {
    private String id;
    private UserDTO user;
    private PostDTO post;
    private ReactionType reactionType;
    private LocalDateTime createAt;
}