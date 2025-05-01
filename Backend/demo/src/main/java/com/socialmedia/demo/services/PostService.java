package com.socialmedia.demo.services;

import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.enums.PrivacySetting;
import com.socialmedia.demo.exceptions.PostNotFoundException;
import com.socialmedia.demo.exceptions.UserNotFoundException;
import com.socialmedia.demo.mappers.PostMapper;
import com.socialmedia.demo.repositories.PostRepository;
import com.socialmedia.demo.repositories.UserRepository;
import com.socialmedia.demo.requests.Post.PostCreateRequest;
import com.socialmedia.demo.requests.Post.PostUpdateRequest;
import com.socialmedia.demo.responses.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository; // Hoặc UserService nếu bạn muốn thêm logic phức tạp hơn
    private final PostMapper postMapper;

    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getAuthorId()));

        Post post = postMapper.toEntity(request);
        post.setAuthor(author); // Thiết lập tác giả cho bài đăng

        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
        return postMapper.toResponse(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        // Lưu ý: Nên xem xét phân trang (Pagination) cho lượng dữ liệu lớn
        return posts.stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByAuthorId(String authorId) {
        // Kiểm tra xem user có tồn tại không trước khi tìm bài đăng
        if (!userRepository.existsById(authorId)) {
            throw new UserNotFoundException("User not found with id: " + authorId);
        }
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return posts.stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByPrivacySetting(PrivacySetting privacySetting) {
        List<Post> posts = postRepository.findByPrivacySetting(privacySetting);
        return posts.stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public PostResponse updatePost(String postId, PostUpdateRequest request) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        // Kiểm tra xem authorId trong request có khớp với author của post hiện tại không (nếu cần)
        // Hoặc có thể thêm logic kiểm tra quyền sửa bài đăng ở đây

        // Sử dụng mapper để cập nhật các trường từ request vào existingPost
        postMapper.updateEntityFromRequest(request, existingPost);

        // Nếu request có authorId mới và khác authorId cũ, cập nhật lại author (cẩn thận với logic này)
        if (request.getAuthorId() != null && !request.getAuthorId().equals(existingPost.getAuthor().getId())) {
             User newAuthor = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new UserNotFoundException("New author not found with id: " + request.getAuthorId()));
             existingPost.setAuthor(newAuthor);
        }


        Post updatedPost = postRepository.save(existingPost);
        return postMapper.toResponse(updatedPost);
    }

    @Transactional
    public void deletePost(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
        // Có thể thêm logic kiểm tra quyền xóa bài đăng ở đây
        postRepository.deleteById(postId);
    }
}