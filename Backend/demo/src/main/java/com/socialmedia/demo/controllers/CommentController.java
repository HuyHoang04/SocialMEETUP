package com.socialmedia.demo.controllers;

import com.socialmedia.demo.requests.Comment.CommentCreateRequest;
import com.socialmedia.demo.requests.Comment.CommentUpdateRequest;
import com.socialmedia.demo.responses.ApiResponse;
import com.socialmedia.demo.responses.CommentResponse;
import com.socialmedia.demo.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder; // Đảm bảo đã import
import org.springframework.security.core.Authentication; // Đảm bảo đã import
import org.springframework.security.access.AccessDeniedException; // Thêm import này

import java.util.List;

@RestController
@RequestMapping("/api/v1") // Base path chung, có thể điều chỉnh nếu cần
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Endpoint để tạo comment mới
    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @RequestBody CommentCreateRequest request // Không cần lấy authorId từ header nữa
            // authorId giờ đã nằm trong request.getAuthorId()
    ) {
        // Gọi service chỉ với request
        CommentResponse createdComment = commentService.createComment(request);
        ApiResponse<CommentResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.CREATED.value())); // 201
        response.setResult(createdComment);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint để lấy thông tin comment bằng ID
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(@PathVariable String commentId) {
        CommentResponse comment = commentService.getCommentById(commentId);
        ApiResponse<CommentResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(comment);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy danh sách comment theo ID bài đăng
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsByPostId(@PathVariable String postId) {
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        ApiResponse<List<CommentResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(comments);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy danh sách comment theo ID tác giả
    @GetMapping("/users/{authorId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsByAuthorId(@PathVariable String authorId) {
        List<CommentResponse> comments = commentService.getCommentsByAuthorId(authorId);
        ApiResponse<List<CommentResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(comments);
        return ResponseEntity.ok(response);
    }

    // Endpoint để cập nhật thông tin comment (Phiên bản an toàn)
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment( // Đổi tên lại
            @PathVariable String commentId,
            @RequestBody CommentUpdateRequest request
    ) {
        // Lấy thông tin xác thực của người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
             throw new AccessDeniedException("User not authenticated");
        }
        String authenticatedAuthorId = authentication.getName(); // Lấy ID người dùng đã xác thực

        // Không cần lấy authorId từ request body nữa
        // String authorIdFromRequest = request.getAuthorId(); // Xóa dòng này
        // if (authorIdFromRequest == null || authorIdFromRequest.trim().isEmpty()) { // Xóa khối if này
        //      throw new IllegalArgumentException("Author ID must be provided in the request for update");
        // }

        // Truyền authenticatedAuthorId vào service để kiểm tra quyền sở hữu comment
        CommentResponse updatedComment = commentService.updateComment(commentId, request, authenticatedAuthorId);

        ApiResponse<CommentResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(updatedComment);
        return ResponseEntity.ok(response);
    }

    // Endpoint để xóa comment (Đã đúng logic lấy authenticatedAuthorId)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @PathVariable String commentId)
            {
        // Lấy thông tin xác thực của người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
             // Xử lý trường hợp người dùng chưa xác thực
             throw new AccessDeniedException("User not authenticated"); // Sử dụng AccessDeniedException
         }
        String authenticatedAuthorId = authentication.getName(); // Lấy ID

        // Truyền authenticatedAuthorId vào service để kiểm tra quyền
        commentService.deleteComment(commentId, authenticatedAuthorId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // Hoặc NO_CONTENT (204)
        response.setResult("Comment deleted successfully with id: " + commentId);
        return ResponseEntity.ok(response); // Hoặc ResponseEntity.noContent().build();
    }
}