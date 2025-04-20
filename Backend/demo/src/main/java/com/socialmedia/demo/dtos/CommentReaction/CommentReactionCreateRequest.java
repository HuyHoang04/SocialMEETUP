package com.socialmedia.demo.dtos.CommentReaction;

import com.socialmedia.demo.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReactionCreateRequest {
    private String commentId;
    private ReactionType reactionType;
}