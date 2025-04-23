package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.entities.PostReaction;
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
public class PostReactionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find a post reaction by ID
     */
    public Optional<PostReaction> findById(Long id) {
        PostReaction postReaction = entityManager.find(PostReaction.class, id);
        return Optional.ofNullable(postReaction);
    }

    /**
     * Find all post reactions
     */
    public List<PostReaction> findAll() {
        return entityManager.createQuery("SELECT pr FROM PostReaction pr", PostReaction.class).getResultList();
    }

    /**
     * Find post reactions by post
     */
    public List<PostReaction> findByPost(Post post) {
        TypedQuery<PostReaction> query = entityManager.createQuery(
            "SELECT pr FROM PostReaction pr WHERE pr.post.id = :postId", PostReaction.class);
        query.setParameter("postId", post.getId());
        return query.getResultList();
    }

    /**
     * Find post reactions by user
     */
    public List<PostReaction> findByUser(User user) {
        TypedQuery<PostReaction> query = entityManager.createQuery(
            "SELECT pr FROM PostReaction pr WHERE pr.user.id = :userId", PostReaction.class);
        query.setParameter("userId", user.getId());
        return query.getResultList();
    }

    /**
     * Find post reaction by user and post
     */
    public Optional<PostReaction> findByUserAndPost(User user, Post post) {
        try {
            TypedQuery<PostReaction> query = entityManager.createQuery(
                "SELECT pr FROM PostReaction pr WHERE pr.user.id = :userId AND pr.post.id = :postId", 
                PostReaction.class);
            query.setParameter("userId", user.getId());
            query.setParameter("postId", post.getId());
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Count reactions by post and reaction type
     */
    public long countByPostAndReactionType(Post post, ReactionType reactionType) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(pr) FROM PostReaction pr WHERE pr.post.id = :postId AND pr.reactionType = :reactionType", 
            Long.class);
        query.setParameter("postId", post.getId());
        query.setParameter("reactionType", reactionType);
        return query.getSingleResult();
    }

    /**
     * Count all reactions by post
     */
    public long countByPost(Post post) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(pr) FROM PostReaction pr WHERE pr.post.id = :postId", Long.class);
        query.setParameter("postId", post.getId());
        return query.getSingleResult();
    }

    /**
     * Save a post reaction
     */
    @Transactional
    public PostReaction save(PostReaction postReaction) {
        if (postReaction.getId() == null) {
            entityManager.persist(postReaction);
            return postReaction;
        } else {
            return entityManager.merge(postReaction);
        }
    }

    /**
     * Delete a post reaction
     */
    @Transactional
    public void delete(PostReaction postReaction) {
        entityManager.remove(entityManager.contains(postReaction) ? 
            postReaction : entityManager.merge(postReaction));
    }

    /**
     * Delete a post reaction by ID
     */
    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Delete post reactions by post
     */
    @Transactional
    public void deleteByPost(Post post) {
        entityManager.createQuery(
            "DELETE FROM PostReaction pr WHERE pr.post.id = :postId")
            .setParameter("postId", post.getId())
            .executeUpdate();
    }

    /**
     * Check if a post reaction exists by ID
     */
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    /**
     * Check if a user has reacted to a post
     */
    public boolean existsByUserAndPost(User user, Post post) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(pr) FROM PostReaction pr WHERE pr.user.id = :userId AND pr.post.id = :postId", 
            Long.class);
        query.setParameter("userId", user.getId());
        query.setParameter("postId", post.getId());
        return query.getSingleResult() > 0;
    }
}