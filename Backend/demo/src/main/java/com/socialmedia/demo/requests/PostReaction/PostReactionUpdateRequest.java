package com.socialmedia.demo.requests.PostReaction;

import com.socialmedia.demo.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostReactionUpdateRequest {
    private String id;
    private String userId;
    private String postId;
    private ReactionType reactionType;
}