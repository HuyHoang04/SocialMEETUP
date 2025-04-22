package com.socialmedia.demo.mappers;

import com.socialmedia.demo.dtos.Post.PostDTO;
import com.socialmedia.demo.dtos.Post.PostCreateRequest;
import com.socialmedia.demo.dtos.Post.PostUpdateRequest;
import com.socialmedia.demo.dtos.Post.PostImageUpdateRequest;
import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.entities.User;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    private final UserMapper userMapper;

    public PostMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Converts Post entity to PostDTO
     */
    public PostDTO toDTO(Post post) {
        if (post == null) {
            return null;
        }
        
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setAuthor(userMapper.toDTO(post.getAuthor()));
        dto.setContent(post.getContent());
        dto.setImageData(post.getImageData());
        dto.setImageType(post.getImageType());
        dto.setCreateAt(post.getCreateAt());
        dto.setPrivacySetting(post.getPrivacySetting());
        
        // Set counts
        if (post.getComments() != null) {
            dto.setCommentCount(post.getComments().size());
        }
        
        if (post.getPostReactions() != null) {
            dto.setReactionCount(post.getPostReactions().size());
        }
        
        return dto;
    }
    
    /**
     * Creates a new Post entity from PostCreateRequest
     */
    public Post toEntity(PostCreateRequest request, User author) {
        if (request == null) {
            return null;
        }
        
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(request.getContent());
        post.setImageData(request.getImageData());
        post.setImageType(request.getImageType());
        post.setPrivacySetting(request.getPrivacySetting());
        
        return post;
    }
    
    /**
     * Updates an existing Post entity from PostUpdateRequest
     */
    public void updateEntityFromDto(PostUpdateRequest request, Post post) {
        if (request == null || post == null) {
            return;
        }
        
        post.setContent(request.getContent());
        post.setPrivacySetting(request.getPrivacySetting());
    }
    
    /**
     * Updates post image from PostImageUpdateRequest
     */
    public void updatePostImage(PostImageUpdateRequest request, Post post) {
        if (request == null || post == null) {
            return;
        }
        
        post.setImageData(request.getImageData());
        post.setImageType(request.getImageType());
    }
    
    /**
     * Removes image from post
     */
    public void removePostImage(Post post) {
        if (post == null) {
            return;
        }
        
        post.setImageData(null);
        post.setImageType(null);
    }
}