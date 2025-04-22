package com.socialmedia.demo.mappers;

import com.socialmedia.demo.dtos.CommentReaction.CommentReactionDTO;
import com.socialmedia.demo.dtos.CommentReaction.CommentReactionCreateRequest;
import com.socialmedia.demo.dtos.CommentReaction.CommentReactionUpdateRequest;
import com.socialmedia.demo.entities.Comment;
import com.socialmedia.demo.entities.CommentReaction;
import com.socialmedia.demo.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentReactionMapper {

    private final UserMapper userMapper;
    private final CommentMapper commentMapper;

    public CommentReactionMapper(UserMapper userMapper, CommentMapper commentMapper) {
        this.userMapper = userMapper;
        this.commentMapper = commentMapper;
    }

    /**
     * Converts CommentReaction entity to CommentReactionDTO
     */
    public CommentReactionDTO toDTO(CommentReaction commentReaction) {
        if (commentReaction == null) {
            return null;
        }
        
        CommentReactionDTO dto = new CommentReactionDTO();
        dto.setId(commentReaction.getId().toString());
        dto.setUser(userMapper.toDTO(commentReaction.getUser()));
        dto.setComment(commentMapper.toDTO(commentReaction.getComment()));
        dto.setReactionType(commentReaction.getReactionType());
        dto.setCreateAt(commentReaction.getCreateAt());
        
        return dto;
    }
    
    /**
     * Creates a new CommentReaction entity from CommentReactionCreateRequest
     */
    public CommentReaction toEntity(CommentReactionCreateRequest request, User user, Comment comment) {
        if (request == null) {
            return null;
        }
        
        CommentReaction commentReaction = new CommentReaction();
        commentReaction.setUser(user);
        commentReaction.setComment(comment);
        commentReaction.setReactionType(request.getReactionType());
        commentReaction.setCreateAt(LocalDateTime.now());
        
        return commentReaction;
    }
    
    /**
     * Updates an existing CommentReaction entity from CommentReactionUpdateRequest
     */
    public void updateEntityFromDto(CommentReactionUpdateRequest request, CommentReaction commentReaction) {
        if (request == null || commentReaction == null) {
            return;
        }
        
        commentReaction.setReactionType(request.getReactionType());
    }
}