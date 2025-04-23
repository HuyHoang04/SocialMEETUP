package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.Comment;
import com.socialmedia.demo.entities.CommentReaction;
import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.enums.ReactionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentReactionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find a comment reaction by ID
     */
    public Optional<CommentReaction> findById(Long id) {
        CommentReaction commentReaction = entityManager.find(CommentReaction.class, id);
        return Optional.ofNullable(commentReaction);
    }

    /**
     * Find all comment reactions
     */
    public List<CommentReaction> findAll() {
        return entityManager.createQuery("SELECT cr FROM CommentReaction cr", CommentReaction.class).getResultList();
    }

    /**
     * Find comment reactions by comment
     */
    public List<CommentReaction> findByComment(Comment comment) {
        TypedQuery<CommentReaction> query = entityManager.createQuery(
            "SELECT cr FROM CommentReaction cr WHERE cr.comment.id = :commentId", CommentReaction.class);
        query.setParameter("commentId", comment.getId());
        return query.getResultList();
    }

    /**
     * Find comment reactions by user
     */
    public List<CommentReaction> findByUser(User user) {
        TypedQuery<CommentReaction> query = entityManager.createQuery(
            "SELECT cr FROM CommentReaction cr WHERE cr.user.id = :userId", CommentReaction.class);
        query.setParameter("userId", user.getId());
        return query.getResultList();
    }

    /**
     * Find comment reaction by user and comment
     */
    public Optional<CommentReaction> findByUserAndComment(User user, Comment comment) {
        try {
            TypedQuery<CommentReaction> query = entityManager.createQuery(
                "SELECT cr FROM CommentReaction cr WHERE cr.user.id = :userId AND cr.comment.id = :commentId", 
                CommentReaction.class);
            query.setParameter("userId", user.getId());
            query.setParameter("commentId", comment.getId());
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Count reactions by comment and reaction type
     */
    public long countByCommentAndReactionType(Comment comment, ReactionType reactionType) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(cr) FROM CommentReaction cr WHERE cr.comment.id = :commentId AND cr.reactionType = :reactionType", 
            Long.class);
        query.setParameter("commentId", comment.getId());
        query.setParameter("reactionType", reactionType);
        return query.getSingleResult();
    }

    /**
     * Count all reactions by comment
     */
    public long countByComment(Comment comment) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(cr) FROM CommentReaction cr WHERE cr.comment.id = :commentId", Long.class);
        query.setParameter("commentId", comment.getId());
        return query.getSingleResult();
    }

    /**
     * Save a comment reaction
     */
    @Transactional
    public CommentReaction save(CommentReaction commentReaction) {
        if (commentReaction.getId() == null) {
            entityManager.persist(commentReaction);
            return commentReaction;
        } else {
            return entityManager.merge(commentReaction);
        }
    }

    /**
     * Delete a comment reaction
     */
    @Transactional
    public void delete(CommentReaction commentReaction) {
        entityManager.remove(entityManager.contains(commentReaction) ? 
            commentReaction : entityManager.merge(commentReaction));
    }

    /**
     * Delete a comment reaction by ID
     */
    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Delete comment reactions by comment
     */
    @Transactional
    public void deleteByComment(Comment comment) {
        entityManager.createQuery(
            "DELETE FROM CommentReaction cr WHERE cr.comment.id = :commentId")
            .setParameter("commentId", comment.getId())
            .executeUpdate();
    }

    /**
     * Check if a comment reaction exists by ID
     */
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    /**
     * Check if a user has reacted to a comment
     */
    public boolean existsByUserAndComment(User user, Comment comment) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(cr) FROM CommentReaction cr WHERE cr.user.id = :userId AND cr.comment.id = :commentId", 
            Long.class);
        query.setParameter("userId", user.getId());
        query.setParameter("commentId", comment.getId());
        return query.getSingleResult() > 0;
    }
}