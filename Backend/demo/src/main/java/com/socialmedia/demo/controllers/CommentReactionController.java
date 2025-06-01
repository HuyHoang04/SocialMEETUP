package com.socialmedia.demo.controllers;

import com.socialmedia.demo.requests.CommentReaction.CommentReactionCreateRequest;
import com.socialmedia.demo.responses.ApiResponse;
import com.socialmedia.demo.responses.CommentReactionResponse;
import com.socialmedia.demo.services.CommentReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1") // Consistent base path
@RequiredArgsConstructor
public class CommentReactionController {

    private final CommentReactionService commentReactionService;

    // Endpoint to create or update a comment reaction
    @PostMapping("/comment-reactions")
    public ResponseEntity<ApiResponse<CommentReactionResponse>> createOrUpdateCommentReaction(
            @RequestBody CommentReactionCreateRequest request) {
        // The service handles finding user/comment and checking existence
        CommentReactionResponse reactionResponse = commentReactionService.createOrUpdateReaction(request);
        ApiResponse<CommentReactionResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200 OK for create/update
        response.setResult(reactionResponse);
        return ResponseEntity.ok(response);
    }

    // Endpoint to delete a comment reaction
    // Uses userId and commentId to identify the specific reaction to delete
    @DeleteMapping("/users/{userId}/comment-reactions/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteCommentReaction(
            @PathVariable String userId,
            @PathVariable String commentId) {
        commentReactionService.deleteReaction(userId, commentId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200 OK or 204 No Content
        response.setResult("Comment reaction deleted successfully for user " + userId + " on comment " + commentId);
        return ResponseEntity.ok(response);
    }

    // Endpoint to get all reactions for a specific comment
    @GetMapping("/comments/{commentId}/reactions")
    public ResponseEntity<ApiResponse<List<CommentReactionResponse>>> getReactionsByCommentId(@PathVariable String commentId) {
        List<CommentReactionResponse> reactions = commentReactionService.getReactionsByCommentId(commentId);
        ApiResponse<List<CommentReactionResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200 OK
        response.setResult(reactions);
        return ResponseEntity.ok(response);
    }

    // Endpoint to get all reactions made by a specific user
    @GetMapping("/users/{userId}/comment-reactions")
    public ResponseEntity<ApiResponse<List<CommentReactionResponse>>> getReactionsByUserId(@PathVariable String userId) {
        List<CommentReactionResponse> reactions = commentReactionService.getReactionsByUserId(userId);
        ApiResponse<List<CommentReactionResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200 OK
        response.setResult(reactions);
        return ResponseEntity.ok(response);
    }

    // Optional: Endpoint to get a specific reaction by its own ID (if needed)
    // @GetMapping("/comment-reactions/{reactionId}")
    // public ResponseEntity<ApiResponse<CommentReactionResponse>> getReactionById(@PathVariable Long reactionId) {
    //     CommentReactionResponse reaction = commentReactionService.getReactionById(reactionId);
    //     ApiResponse<CommentReactionResponse> response = new ApiResponse<>();
    //     response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
    //     response.setResult(reaction);
    //     return ResponseEntity.ok(response);
    // }
}