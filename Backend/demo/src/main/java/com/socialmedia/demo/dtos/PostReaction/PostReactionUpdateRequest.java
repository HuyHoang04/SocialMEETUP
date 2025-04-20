package com.socialmedia.demo.dtos.PostReaction;

import com.socialmedia.demo.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostReactionUpdateRequest {
    private String id;
    private ReactionType reactionType;
}