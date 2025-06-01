package com.socialmedia.demo.services;

import com.socialmedia.demo.entities.Comment;
import com.socialmedia.demo.entities.CommentReaction;
import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.exceptions.CommentNotFoundException;
import com.socialmedia.demo.exceptions.CommentReactionNotFoundException;
import com.socialmedia.demo.exceptions.UserNotFoundException;
import com.socialmedia.demo.mappers.CommentReactionMapper; // Assuming this mapper exists
import com.socialmedia.demo.repositories.CommentReactionRepository;
import com.socialmedia.demo.repositories.CommentRepository;
import com.socialmedia.demo.repositories.UserRepository;
import com.socialmedia.demo.requests.CommentReaction.CommentReactionCreateRequest; // Assuming this request exists
import com.socialmedia.demo.responses.CommentReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentReactionService {

    private final CommentReactionRepository commentReactionRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentReactionMapper commentReactionMapper; // Inject the mapper

    @Transactional
    public CommentReactionResponse createOrUpdateReaction(CommentReactionCreateRequest request) {
        String userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        String commentId = request.getCommentId();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        Optional<CommentReaction> existingReactionOpt = commentReactionRepository.findByUserIdAndCommentId(userId, commentId);

        CommentReaction commentReaction;
        if (existingReactionOpt.isPresent()) {
            // Update existing reaction
            commentReaction = existingReactionOpt.get();
            // Optional: Add security check if needed
            // if (!commentReaction.getUser().getId().equals(userId)) {
            //     throw new SecurityException("User mismatch when updating reaction.");
            // }
            commentReaction.setReactionType(request.getReactionType());
        } else {
            // Create new reaction
            commentReaction = commentReactionMapper.toEntity(request); // Mapper should handle basic fields
            commentReaction.setUser(user);
            commentReaction.setComment(comment);
            // reactionType is set via mapper from request
        }

        CommentReaction savedReaction = commentReactionRepository.save(commentReaction);
        return commentReactionMapper.toResponse(savedReaction);
    }

    @Transactional
    public void deleteReaction(String userId, String commentId) {
        CommentReaction reaction = commentReactionRepository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(() -> new CommentReactionNotFoundException("Reaction not found for user " + userId + " on comment " + commentId));

        // Optional: Add authorization check here if needed, comparing userId with authenticated user
        // Example: if (!reaction.getUser().getId().equals(authenticatedUserId)) { throw new AccessDeniedException(...) }

        commentReactionRepository.delete(reaction);
    }

    @Transactional(readOnly = true)
    public List<CommentReactionResponse> getReactionsByCommentId(String commentId) {
        // Check if comment exists
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found with id: " + commentId);
        }
        List<CommentReaction> reactions = commentReactionRepository.findByCommentId(commentId);
        return reactions.stream()
                .map(commentReactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentReactionResponse> getReactionsByUserId(String userId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        List<CommentReaction> reactions = commentReactionRepository.findByUserId(userId);
        return reactions.stream()
                .map(commentReactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Optional: Get a specific reaction by its own ID (if needed)
    @Transactional(readOnly = true)
    public CommentReactionResponse getReactionById(Long reactionId) {
        CommentReaction reaction = commentReactionRepository.findById(reactionId)
                .orElseThrow(() -> new CommentReactionNotFoundException("CommentReaction not found with id: " + reactionId));
        return commentReactionMapper.toResponse(reaction);
    }
}