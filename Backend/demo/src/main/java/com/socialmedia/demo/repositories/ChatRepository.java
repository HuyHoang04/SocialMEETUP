package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.Chat;
import com.socialmedia.demo.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChatRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find a chat by ID
     */
    public Optional<Chat> findById(String id) {
        Chat chat = entityManager.find(Chat.class, id);
        return Optional.ofNullable(chat);
    }

    /**
     * Find all chats
     */
    public List<Chat> findAll() {
        return entityManager.createQuery("SELECT c FROM Chat c", Chat.class).getResultList();
    }

    /**
     * Find chats by user
     */
    public List<Chat> findByUser(User user) {
        TypedQuery<Chat> query = entityManager.createQuery(
            "SELECT c FROM Chat c JOIN c.members m WHERE m.id = :userId", Chat.class);
        query.setParameter("userId", user.getId());
        return query.getResultList();
    }

    /**
     * Find chat between two users (direct chat)
     */
    public Optional<Chat> findDirectChat(User user1, User user2) {
        try {
            TypedQuery<Chat> query = entityManager.createQuery(
                "SELECT c FROM Chat c JOIN c.members m1 JOIN c.members m2 " +
                "WHERE m1.id = :user1Id AND m2.id = :user2Id " +
                "AND (SELECT COUNT(m) FROM c.members m) = 2", Chat.class);
            query.setParameter("user1Id", user1.getId());
            query.setParameter("user2Id", user2.getId());
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Save a chat
     */
    @Transactional
    public Chat save(Chat chat) {
        if (chat.getId() == null) {
            entityManager.persist(chat);
            return chat;
        } else {
            return entityManager.merge(chat);
        }
    }

    /**
     * Delete a chat
     */
    @Transactional
    public void delete(Chat chat) {
        entityManager.remove(entityManager.contains(chat) ? chat : entityManager.merge(chat));
    }

    /**
     * Delete a chat by ID
     */
    @Transactional
    public void deleteById(String id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Check if a chat exists by ID
     */
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    /**
     * Find chats by name containing the search term (for group chats)
     */
    public List<Chat> searchChats(String searchTerm, User user) {
        TypedQuery<Chat> query = entityManager.createQuery(
            "SELECT DISTINCT c FROM Chat c JOIN c.members m " +
            "WHERE m.id = :userId AND " +
            "(c.id LIKE :searchTerm OR EXISTS (SELECT m2 FROM c.members m2 WHERE LOWER(m2.username) LIKE LOWER(:searchTerm)))", 
            Chat.class);
        query.setParameter("userId", user.getId());
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    /**
     * Count unread messages in a chat for a specific user
     */
    public long countUnreadMessages(Chat chat, User user) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM Message m " +
            "WHERE m.chat.id = :chatId " +
            "AND m.sender.id != :userId " +
            "AND (m.messageStatus != 'READ' OR m.messageStatus IS NULL)", 
            Long.class);
        query.setParameter("chatId", chat.getId());
        query.setParameter("userId", user.getId());
        return query.getSingleResult();
    }
}