package com.socialmedia.demo.requests.CommentReaction;

import com.socialmedia.demo.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReactionCreateRequest {
    private String userId;
    private String commentId;
    private ReactionType reactionType;
}