package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.Chat;
import com.socialmedia.demo.entities.Message;
import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.enums.MessageStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MessageRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find a message by ID
     */
    public Optional<Message> findById(String id) {
        Message message = entityManager.find(Message.class, id);
        return Optional.ofNullable(message);
    }

    /**
     * Find all messages
     */
    public List<Message> findAll() {
        return entityManager.createQuery("SELECT m FROM Message m", Message.class).getResultList();
    }

    /**
     * Find messages by chat
     */
    public List<Message> findByChat(Chat chat) {
        TypedQuery<Message> query = entityManager.createQuery(
            "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sendAt ASC", Message.class);
        query.setParameter("chatId", chat.getId());
        return query.getResultList();
    }

    /**
     * Find messages by chat with pagination
     */
    public List<Message> findByChat(Chat chat, int page, int size) {
        TypedQuery<Message> query = entityManager.createQuery(
            "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sendAt ASC", Message.class);
        query.setParameter("chatId", chat.getId());
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Find messages by sender
     */
    public List<Message> findBySender(User sender) {
        TypedQuery<Message> query = entityManager.createQuery(
            "SELECT m FROM Message m WHERE m.sender.id = :senderId ORDER BY m.sendAt DESC", Message.class);
        query.setParameter("senderId", sender.getId());
        return query.getResultList();
    }

    /**
     * Find unread messages in a chat for a user
     */
    public List<Message> findUnreadMessagesInChat(Chat chat, User user) {
        TypedQuery<Message> query = entityManager.createQuery(
            "SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.sender.id != :userId " +
            "AND (m.messageStatus != 'READ' OR m.messageStatus IS NULL) ORDER BY m.sendAt ASC", 
            Message.class);
        query.setParameter("chatId", chat.getId());
        query.setParameter("userId", user.getId());
        return query.getResultList();
    }

    /**
     * Count unread messages in a chat for a user
     */
    public long countUnreadMessagesInChat(Chat chat, User user) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId AND m.sender.id != :userId " +
            "AND (m.messageStatus != 'READ' OR m.messageStatus IS NULL)", 
            Long.class);
        query.setParameter("chatId", chat.getId());
        query.setParameter("userId", user.getId());
        return query.getSingleResult();
    }

    /**
     * Save a message
     */
    @Transactional
    public Message save(Message message) {
        if (message.getId() == null) {
            entityManager.persist(message);
            return message;
        } else {
            return entityManager.merge(message);
        }
    }

    /**
     * Delete a message
     */
    @Transactional
    public void delete(Message message) {
        entityManager.remove(entityManager.contains(message) ? message : entityManager.merge(message));
    }

    /**
     * Delete a message by ID
     */
    @Transactional
    public void deleteById(String id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Delete messages by chat
     */
    @Transactional
    public void deleteByChat(Chat chat) {
        entityManager.createQuery("DELETE FROM Message m WHERE m.chat.id = :chatId")
            .setParameter("chatId", chat.getId())
            .executeUpdate();
    }

    /**
     * Check if a message exists by ID
     */
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    /**
     * Update message status
     */
    @Transactional
    public void updateMessageStatus(Message message, MessageStatus status) {
        message.setMessageStatus(status);
        entityManager.merge(message);
    }

    /**
     * Update status of all messages in a chat for a user
     */
    @Transactional
    public void updateAllMessageStatusInChat(Chat chat, User user, MessageStatus status) {
        entityManager.createQuery(
            "UPDATE Message m SET m.messageStatus = :status " +
            "WHERE m.chat.id = :chatId AND m.sender.id != :userId " +
            "AND (m.messageStatus != :status OR m.messageStatus IS NULL)")
            .setParameter("status", status)
            .setParameter("chatId", chat.getId())
            .setParameter("userId", user.getId())
            .executeUpdate();
    }
}