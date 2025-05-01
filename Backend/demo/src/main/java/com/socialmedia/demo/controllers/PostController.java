package com.socialmedia.demo.controllers;

import com.socialmedia.demo.enums.PrivacySetting;
import com.socialmedia.demo.requests.Post.PostCreateRequest;
import com.socialmedia.demo.requests.Post.PostUpdateRequest;
import com.socialmedia.demo.responses.ApiResponse;
import com.socialmedia.demo.responses.PostResponse;
import com.socialmedia.demo.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts") // Base path cho các endpoint liên quan đến Post
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // Endpoint để tạo bài đăng mới
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@RequestBody PostCreateRequest request) {
        PostResponse createdPost = postService.createPost(request);
        ApiResponse<PostResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.CREATED.value())); // 201
        response.setResult(createdPost);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint để lấy thông tin bài đăng bằng ID
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable String postId) {
        PostResponse post = postService.getPostById(postId);
        ApiResponse<PostResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(post);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy danh sách tất cả bài đăng
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        ApiResponse<List<PostResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(posts);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy danh sách bài đăng theo ID tác giả
    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByAuthorId(@PathVariable String authorId) {
        List<PostResponse> posts = postService.getPostsByAuthorId(authorId);
        ApiResponse<List<PostResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(posts);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy danh sách bài đăng theo cài đặt riêng tư
    @GetMapping("/privacy/{privacySetting}")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByPrivacySetting(@PathVariable PrivacySetting privacySetting) {
        List<PostResponse> posts = postService.getPostsByPrivacySetting(privacySetting);
        ApiResponse<List<PostResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(posts);
        return ResponseEntity.ok(response);
    }

    // Endpoint để cập nhật thông tin bài đăng
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(@PathVariable String postId, @RequestBody PostUpdateRequest request) {
        PostResponse updatedPost = postService.updatePost(postId, request);
        ApiResponse<PostResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(updatedPost);
        return ResponseEntity.ok(response);
    }

    // Endpoint để xóa bài đăng
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // Hoặc NO_CONTENT (204)
        response.setResult("Post deleted successfully with id: " + postId);
        return ResponseEntity.ok(response); // Hoặc ResponseEntity.noContent().build();
    }
}