package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {

    // Find all chats where the given user ID is a member
    // Spring Data JPA can derive this query from the method name and the relationship in the Chat entity
    List<Chat> findByMembers_Id(String userId);

    // Optional: Find a specific chat between exactly two users (more complex query)
    // This query finds chats that have exactly two members AND both specified user IDs are present in the members list.
    @Query("SELECT c FROM Chat c JOIN c.members m1 JOIN c.members m2 WHERE m1.id = :userId1 AND m2.id = :userId2 AND SIZE(c.members) = 2")
    Optional<Chat> findDirectChatBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    // You can add other custom query methods here as needed.
}