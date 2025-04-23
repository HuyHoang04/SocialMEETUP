package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.enums.PrivacySetting;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find a post by ID
     */
    public Optional<Post> findById(String id) {
        Post post = entityManager.find(Post.class, id);
        return Optional.ofNullable(post);
    }

    /**
     * Find all posts
     */
    public List<Post> findAll() {
        return entityManager.createQuery("SELECT p FROM Post p ORDER BY p.createAt DESC", Post.class).getResultList();
    }

    /**
     * Find posts with pagination
     */
    public List<Post> findAll(int page, int size) {
        TypedQuery<Post> query = entityManager.createQuery(
            "SELECT p FROM Post p ORDER BY p.createAt DESC", Post.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Find posts by author
     */
    public List<Post> findByAuthor(User author) {
        TypedQuery<Post> query = entityManager.createQuery(
            "SELECT p FROM Post p WHERE p.author.id = :authorId ORDER BY p.createAt DESC", Post.class);
        query.setParameter("authorId", author.getId());
        return query.getResultList();
    }

    /**
     * Find posts by author with pagination
     */
    public List<Post> findByAuthor(User author, int page, int size) {
        TypedQuery<Post> query = entityManager.createQuery(
            "SELECT p FROM Post p WHERE p.author.id = :authorId ORDER BY p.createAt DESC", Post.class);
        query.setParameter("authorId", author.getId());
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Find posts for user's feed (posts from user and their friends with appropriate privacy settings)
     */
    public List<Post> findPostsForFeed(User user, List<String> friendIds, int page, int size) {
        // Add user's own ID to the list of IDs to include
        friendIds.add(user.getId());
        
        TypedQuery<Post> query = entityManager.createQuery(
            "SELECT p FROM Post p WHERE " +
            "(p.author.id = :userId) OR " + // User's own posts
            "(p.author.id IN :friendIds AND (p.privacySetting = :friendsOnly OR p.privacySetting = :public))", 
            Post.class);
        query.setParameter("userId", user.getId());
        query.setParameter("friendIds", friendIds);
        query.setParameter("friendsOnly", PrivacySetting.FRIENDS_ONLY);
        query.setParameter("public", PrivacySetting.PUBLIC);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Find public posts
     */
    public List<Post> findPublicPosts(int page, int size) {
        TypedQuery<Post> query = entityManager.createQuery(
            "SELECT p FROM Post p WHERE p.privacySetting = :public ORDER BY p.createAt DESC", Post.class);
        query.setParameter("public", PrivacySetting.PUBLIC);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Search posts by content
     */
    public List<Post> searchByContent(String searchTerm, int page, int size) {
        TypedQuery<Post> query = entityManager.createQuery(
            "SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(:searchTerm) ORDER BY p.createAt DESC", 
            Post.class);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Save a post
     */
    @Transactional
    public Post save(Post post) {
        if (post.getId() == null) {
            entityManager.persist(post);
            return post;
        } else {
            return entityManager.merge(post);
        }
    }

    /**
     * Delete a post
     */
    @Transactional
    public void delete(Post post) {
        entityManager.remove(entityManager.contains(post) ? post : entityManager.merge(post));
    }

    /**
     * Delete a post by ID
     */
    @Transactional
    public void deleteById(String id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Check if a post exists by ID
     */
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    /**
     * Count posts by author
     */
    public long countByAuthor(User author) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Post p WHERE p.author.id = :authorId", Long.class);
        query.setParameter("authorId", author.getId());
        return query.getSingleResult();
    }
}