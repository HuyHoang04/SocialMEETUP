package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    // Find all messages belonging to a specific chat, ordered by send time
    List<Message> findByChatIdOrderBySendAtAsc(String chatId);

    // You can add other custom query methods here as needed.
    // For example, find messages sent by a specific user:
    // List<Message> findBySenderId(String senderId);

    // Or find messages containing specific content (case-insensitive):
    // List<Message> findByContentContainingIgnoreCase(String keyword);
}