package com.socialmedia.demo.responses;

import com.socialmedia.demo.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostReactionResponse {
    private String id;
    private UserResponse user;
    private String postId;
    private ReactionType reactionType;
    private LocalDateTime createAt;
}