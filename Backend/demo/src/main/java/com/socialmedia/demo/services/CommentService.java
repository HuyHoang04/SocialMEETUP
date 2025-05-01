package com.socialmedia.demo.services;

import com.socialmedia.demo.entities.Comment;
import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.exceptions.CommentNotFoundException;
import com.socialmedia.demo.exceptions.PostNotFoundException;
import com.socialmedia.demo.exceptions.UserNotFoundException;
import com.socialmedia.demo.mappers.CommentMapper;
import com.socialmedia.demo.repositories.CommentRepository;
import com.socialmedia.demo.repositories.PostRepository;
import com.socialmedia.demo.repositories.UserRepository;
import com.socialmedia.demo.requests.Comment.CommentCreateRequest;
import com.socialmedia.demo.requests.Comment.CommentUpdateRequest;
import com.socialmedia.demo.responses.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException; // Thêm import này
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    @Transactional
    // Loại bỏ tham số authorId riêng biệt
    public CommentResponse createComment(CommentCreateRequest request) {
        // Lấy authorId từ request
        String authorIdFromRequest = request.getAuthorId();
        if (authorIdFromRequest == null || authorIdFromRequest.trim().isEmpty()) {
            // Xử lý trường hợp authorId bị thiếu trong request nếu cần
            throw new IllegalArgumentException("Author ID must be provided in the request");
        }

        User author = userRepository.findById(authorIdFromRequest) // Sử dụng authorId từ request
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + authorIdFromRequest));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + request.getPostId()));

        Comment comment = commentMapper.toEntity(request);
        comment.setAuthor(author);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentById(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));
        return commentMapper.toResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(String postId) {
        // Kiểm tra xem post có tồn tại không
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByAuthorId(String authorId) {
        // Kiểm tra xem user có tồn tại không
        if (!userRepository.existsById(authorId)) {
            throw new UserNotFoundException("User not found with id: " + authorId);
        }
        List<Comment> comments = commentRepository.findByAuthorId(authorId);
        return comments.stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    // Giữ nguyên tham số authorId để kiểm tra quyền
    public CommentResponse updateComment(String commentId, CommentUpdateRequest request, String authenticatedAuthorId) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        // Kiểm tra quyền sử dụng authenticatedAuthorId (ID của người dùng đã xác thực)
        if (!existingComment.getAuthor().getId().equals(authenticatedAuthorId)) {
            throw new AccessDeniedException("User is not authorized to update this comment");
        }

        // Lưu ý: Không nên sử dụng request.getAuthorId() ở đây để thay đổi tác giả
        // Việc cập nhật tác giả thường không được phép hoặc cần logic đặc biệt

        // Kiểm tra xem postId trong request có khớp với postId của comment hiện tại không (nếu cần)
        if (request.getPostId() != null && !request.getPostId().equals(existingComment.getPost().getId())) {
             // Bạn có thể quyết định không cho phép thay đổi post của comment hoặc tìm post mới
             // Ví dụ: throw new IllegalArgumentException("Cannot change the post of a comment");
             // Hoặc:
             Post newPost = postRepository.findById(request.getPostId())
                 .orElseThrow(() -> new PostNotFoundException("New post not found with id: " + request.getPostId()));
             existingComment.setPost(newPost); // Cẩn thận với logic này
        }


        // Sử dụng mapper để cập nhật các trường từ request vào existingComment
        commentMapper.updateEntityFromRequest(request, existingComment);

        Comment updatedComment = commentRepository.save(existingComment);
        return commentMapper.toResponse(updatedComment);
    }

    @Transactional
    // Giữ nguyên tham số authorId để kiểm tra quyền
    public void deleteComment(String commentId, String authenticatedAuthorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        // Kiểm tra quyền sử dụng authenticatedAuthorId (ID của người dùng đã xác thực)
        if (!comment.getAuthor().getId().equals(authenticatedAuthorId)) {
            throw new AccessDeniedException("User is not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}