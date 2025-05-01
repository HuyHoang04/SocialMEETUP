package com.socialmedia.demo.services;

import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.entities.PostReaction;
import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.exceptions.PostNotFoundException;
import com.socialmedia.demo.exceptions.PostReactionNotFoundException;
import com.socialmedia.demo.exceptions.UserNotFoundException;
import com.socialmedia.demo.mappers.PostReactionMapper;
import com.socialmedia.demo.repositories.PostReactionRepository;
import com.socialmedia.demo.repositories.PostRepository;
import com.socialmedia.demo.repositories.UserRepository;
import com.socialmedia.demo.requests.PostReaction.PostReactionCreateRequest;
import com.socialmedia.demo.responses.PostReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostReactionService {

    private final PostReactionRepository postReactionRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostReactionMapper postReactionMapper;
    // Giả sử bạn có một cách để lấy user ID của người dùng hiện tại, ví dụ từ SecurityContext
    // private final UserService userService; // Hoặc một cách khác để lấy user ID

    @Transactional
    public PostReactionResponse createOrUpdateReaction(PostReactionCreateRequest request) { // Loại bỏ currentUserId
        // Lấy userId từ request
        String userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + request.getPostId()));

        // Sử dụng userId từ request để tìm reaction hiện có
        Optional<PostReaction> existingReactionOpt = postReactionRepository.findByUserIdAndPostId(userId, request.getPostId());

        PostReaction postReaction;
        if (existingReactionOpt.isPresent()) {
            // Nếu đã reaction, cập nhật loại reaction
            postReaction = existingReactionOpt.get();
            // Kiểm tra xem user trong request có khớp với user của reaction không (tùy chọn, để bảo mật)
            if (!postReaction.getUser().getId().equals(userId)) {
                 // Có thể throw exception hoặc xử lý khác nếu user không khớp
                 throw new SecurityException("User mismatch when updating reaction.");
            }
            postReaction.setReactionType(request.getReactionType());
        } else {
            // Nếu chưa reaction, tạo reaction mới
            postReaction = postReactionMapper.toEntity(request); // Mapper vẫn bỏ qua user và post
            postReaction.setUser(user); // Set user lấy được từ userId trong request
            postReaction.setPost(post);
            // reactionType đã được map từ request
        }

        PostReaction savedReaction = postReactionRepository.save(postReaction);
        return postReactionMapper.toResponse(savedReaction);
    }

    @Transactional
    public void deleteReaction(String userId, String postId) { // Thay currentUserId bằng userId
        PostReaction reaction = postReactionRepository.findByUserIdAndPostId(userId, postId) // Sử dụng userId được truyền vào
                .orElseThrow(() -> new PostReactionNotFoundException("Reaction not found for user " + userId + " on post " + postId));

        // Optional: Kiểm tra xem người dùng yêu cầu xóa có phải là chủ sở hữu reaction không
        // (Cần cơ chế xác thực để lấy ID người dùng hiện tại nếu logic này cần thiết)
        // Ví dụ: if (!reaction.getUser().getId().equals(authenticatedUserId)) { throw new AccessDeniedException(...) }

        postReactionRepository.delete(reaction);
    }

    @Transactional(readOnly = true)
    public List<PostReactionResponse> getReactionsByPostId(String postId) {
        // Kiểm tra xem post có tồn tại không
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
        List<PostReaction> reactions = postReactionRepository.findByPostId(postId);
        return reactions.stream()
                .map(postReactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostReactionResponse> getReactionsByUserId(String userId) {
        // Kiểm tra xem user có tồn tại không
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        List<PostReaction> reactions = postReactionRepository.findByUserId(userId);
        return reactions.stream()
                .map(postReactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Có thể thêm các phương thức khác nếu cần, ví dụ:
    // - Lấy reaction cụ thể bằng ID
    // - Đếm số lượng reaction theo từng loại cho một bài đăng
}