package com.socialmedia.demo.dtos.CommentReaction;

import com.socialmedia.demo.dtos.User.UserDTO;
import com.socialmedia.demo.dtos.Comment.CommentDTO;
import com.socialmedia.demo.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReactionDTO {
    private String id;
    private UserDTO user;
    private CommentDTO comment;
    private ReactionType reactionType;
    private LocalDateTime createAt;
}