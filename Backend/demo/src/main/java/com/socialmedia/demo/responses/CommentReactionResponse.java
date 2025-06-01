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
public class CommentReactionResponse {
    private String id; // Using String for consistency, map from Long in entity
    private UserResponse user; // Response object for user details
    private String commentId; // ID of the related comment
    private ReactionType reactionType;
    private LocalDateTime createAt;
}