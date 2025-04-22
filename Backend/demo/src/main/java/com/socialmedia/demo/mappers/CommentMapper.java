package com.socialmedia.demo.mappers;

import com.socialmedia.demo.dtos.Comment.CommentDTO;
import com.socialmedia.demo.dtos.Comment.CommentCreateRequest;
import com.socialmedia.demo.dtos.Comment.CommentUpdateRequest;
import com.socialmedia.demo.entities.Comment;
import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    private final UserMapper userMapper;

    public CommentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Converts Comment entity to CommentDTO
     */
    public CommentDTO toDTO(Comment comment) {
        if (comment == null) {
            return null;
        }
        
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setAuthor(userMapper.toDTO(comment.getAuthor()));
        dto.setPostId(comment.getPost().getId());
        dto.setContent(comment.getContent());
        dto.setImageData(comment.getImageData());
        dto.setImageType(comment.getImageType());
        dto.setCreateAt(comment.getCreateAt());
        
        // Set reaction count
        if (comment.getCommentReactions() != null) {
            dto.setReactionCount(comment.getCommentReactions().size());
        }
        
        return dto;
    }
    
    /**
     * Creates a new Comment entity from CommentCreateRequest
     */
    public Comment toEntity(CommentCreateRequest request, User author, Post post) {
        if (request == null) {
            return null;
        }
        
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setContent(request.getContent());
        comment.setImageData(request.getImageData());
        comment.setImageType(request.getImageType());
        comment.setCreateAt(LocalDateTime.now());
        
        return comment;
    }
    
    /**
     * Updates an existing Comment entity from CommentUpdateRequest
     */
    public void updateEntityFromDto(CommentUpdateRequest request, Comment comment) {
        if (request == null || comment == null) {
            return;
        }
        
        comment.setContent(request.getContent());
    }
    
    /**
     * Updates comment image
     */
    public void updateCommentImage(byte[] imageData, String imageType, Comment comment) {
        if (comment == null || imageData == null) {
            return;
        }
        
        comment.setImageData(imageData);
        comment.setImageType(imageType);
    }
    
    /**
     * Removes image from comment
     */
    public void removeCommentImage(Comment comment) {
        if (comment == null) {
            return;
        }
        
        comment.setImageData(null);
        comment.setImageType(null);
    }
}