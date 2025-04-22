package com.socialmedia.demo.mappers;

import com.socialmedia.demo.dtos.PostReaction.PostReactionDTO;
import com.socialmedia.demo.dtos.PostReaction.PostReactionCreateRequest;
import com.socialmedia.demo.dtos.PostReaction.PostReactionUpdateRequest;
import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.entities.PostReaction;
import com.socialmedia.demo.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostReactionMapper {

    private final UserMapper userMapper;
    private final PostMapper postMapper;

    public PostReactionMapper(UserMapper userMapper, PostMapper postMapper) {
        this.userMapper = userMapper;
        this.postMapper = postMapper;
    }

    /**
     * Converts PostReaction entity to PostReactionDTO
     */
    public PostReactionDTO toDTO(PostReaction postReaction) {
        if (postReaction == null) {
            return null;
        }
        
        PostReactionDTO dto = new PostReactionDTO();
        dto.setId(postReaction.getId().toString());
        dto.setUser(userMapper.toDTO(postReaction.getUser()));
        dto.setPost(postMapper.toDTO(postReaction.getPost()));
        dto.setReactionType(postReaction.getReactionType());
        dto.setCreateAt(postReaction.getCreateAt());
        
        return dto;
    }
    
    /**
     * Creates a new PostReaction entity from PostReactionCreateRequest
     */
    public PostReaction toEntity(PostReactionCreateRequest request, User user, Post post) {
        if (request == null) {
            return null;
        }
        
        PostReaction postReaction = new PostReaction();
        postReaction.setUser(user);
        postReaction.setPost(post);
        postReaction.setReactionType(request.getReactionType());
        postReaction.setCreateAt(LocalDateTime.now());
        
        return postReaction;
    }
    
    /**
     * Updates an existing PostReaction entity from PostReactionUpdateRequest
     */
    public void updateEntityFromDto(PostReactionUpdateRequest request, PostReaction postReaction) {
        if (request == null || postReaction == null) {
            return;
        }
        
        postReaction.setReactionType(request.getReactionType());
    }
    
    /**
     * Creates a simplified DTO with minimal information
     */
    public PostReactionDTO toSimpleDTO(PostReaction postReaction) {
        if (postReaction == null) {
            return null;
        }
        
        PostReactionDTO dto = new PostReactionDTO();
        dto.setId(postReaction.getId().toString());
        dto.setUser(userMapper.toDTO(postReaction.getUser()));
        dto.setReactionType(postReaction.getReactionType());
        
        return dto;
    }
}