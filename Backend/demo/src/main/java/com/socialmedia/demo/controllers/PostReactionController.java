package com.socialmedia.demo.controllers;

import com.socialmedia.demo.requests.PostReaction.PostReactionCreateRequest;
import com.socialmedia.demo.responses.ApiResponse;
import com.socialmedia.demo.responses.PostReactionResponse;
import com.socialmedia.demo.services.PostReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1") // Base path chung
@RequiredArgsConstructor
public class PostReactionController {

    private final PostReactionService postReactionService;

    // Endpoint để tạo hoặc cập nhật reaction
    @PostMapping("/reactions")
    public ResponseEntity<ApiResponse<PostReactionResponse>> createOrUpdateReaction(
            @RequestBody PostReactionCreateRequest request) { // Không cần lấy currentUserId riêng

        // UserId giờ đã có trong request.getUserId()
        PostReactionResponse reactionResponse = postReactionService.createOrUpdateReaction(request); // Gọi service chỉ với request
        ApiResponse<PostReactionResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value()));
        response.setResult(reactionResponse);
        return ResponseEntity.ok(response);
    }

    // Endpoint để xóa reaction
    // Thay đổi mapping để nhận cả userId và postId
    @DeleteMapping("/users/{userId}/reactions/posts/{postId}")
    public ResponseEntity<ApiResponse<String>> deleteReaction(
            @PathVariable String userId, // Nhận userId từ path
            @PathVariable String postId) {

        // Không cần lấy currentUserId riêng nữa
        postReactionService.deleteReaction(userId, postId); // Gọi service với userId và postId từ path
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value()));
        response.setResult("Reaction deleted successfully for user " + userId + " on post: " + postId);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy tất cả reactions của một bài đăng cụ thể
    @GetMapping("/posts/{postId}/reactions")
    public ResponseEntity<ApiResponse<List<PostReactionResponse>>> getReactionsByPostId(@PathVariable String postId) {
        List<PostReactionResponse> reactions = postReactionService.getReactionsByPostId(postId);
        ApiResponse<List<PostReactionResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(reactions);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy tất cả reactions của một người dùng cụ thể (giữ nguyên)
    @GetMapping("/users/{userId}/reactions")
    public ResponseEntity<ApiResponse<List<PostReactionResponse>>> getReactionsByUserId(@PathVariable String userId) {
        List<PostReactionResponse> reactions = postReactionService.getReactionsByUserId(userId);
        ApiResponse<List<PostReactionResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(reactions);
        return ResponseEntity.ok(response);
    }
}