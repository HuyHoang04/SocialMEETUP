package com.socialmedia.demo.dtos.CommentReaction;

import com.socialmedia.demo.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReactionUpdateRequest {
    private String id;
    private ReactionType reactionType;
}