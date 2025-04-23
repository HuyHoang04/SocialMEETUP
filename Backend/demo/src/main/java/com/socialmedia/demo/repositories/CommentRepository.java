package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.Comment;
import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find a comment by ID
     */
    public Optional<Comment> findById(String id) {
        Comment comment = entityManager.find(Comment.class, id);
        return Optional.ofNullable(comment);
    }

    /**
     * Find all comments
     */
    public List<Comment> findAll() {
        return entityManager.createQuery("SELECT c FROM Comment c", Comment.class).getResultList();
    }

    /**
     * Find comments by post
     */
    public List<Comment> findByPost(Post post) {
        TypedQuery<Comment> query = entityManager.createQuery(
            "SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createAt DESC", Comment.class);
        query.setParameter("postId", post.getId());
        return query.getResultList();
    }

    /**
     * Find comments by post with pagination
     */
    public List<Comment> findByPost(Post post, int page, int size) {
        TypedQuery<Comment> query = entityManager.createQuery(
            "SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createAt DESC", Comment.class);
        query.setParameter("postId", post.getId());
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Find comments by author
     */
    public List<Comment> findByAuthor(User author) {
        TypedQuery<Comment> query = entityManager.createQuery(
            "SELECT c FROM Comment c WHERE c.author.id = :authorId ORDER BY c.createAt DESC", Comment.class);
        query.setParameter("authorId", author.getId());
        return query.getResultList();
    }

    /**
     * Count comments by post
     */
    public long countByPost(Post post) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId", Long.class);
        query.setParameter("postId", post.getId());
        return query.getSingleResult();
    }

    /**
     * Save a comment
     */
    @Transactional
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            entityManager.persist(comment);
            return comment;
        } else {
            return entityManager.merge(comment);
        }
    }

    /**
     * Delete a comment
     */
    @Transactional
    public void delete(Comment comment) {
        entityManager.remove(entityManager.contains(comment) ? comment : entityManager.merge(comment));
    }

    /**
     * Delete a comment by ID
     */
    @Transactional
    public void deleteById(String id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Check if a comment exists by ID
     */
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    /**
     * Search comments by content
     */
    public List<Comment> searchByContent(String searchTerm) {
        TypedQuery<Comment> query = entityManager.createQuery(
            "SELECT c FROM Comment c WHERE LOWER(c.content) LIKE LOWER(:searchTerm) ORDER BY c.createAt DESC", 
            Comment.class);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    /**
     * Find recent comments
     */
    public List<Comment> findRecentComments(int limit) {
        TypedQuery<Comment> query = entityManager.createQuery(
            "SELECT c FROM Comment c ORDER BY c.createAt DESC", Comment.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}