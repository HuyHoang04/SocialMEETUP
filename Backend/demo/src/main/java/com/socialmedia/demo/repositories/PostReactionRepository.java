package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    // Find reactions by post ID
    List<PostReaction> findByPostId(String postId);

    // Find reactions by user ID
    List<PostReaction> findByUserId(String userId);

    // Find a specific reaction by user ID and post ID (useful for checking if a user already reacted)
    Optional<PostReaction> findByUserIdAndPostId(String userId, String postId);

    // You can add more custom query methods here as needed.
}