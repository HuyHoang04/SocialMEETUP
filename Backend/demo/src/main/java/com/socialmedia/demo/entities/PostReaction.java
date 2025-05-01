package com.socialmedia.demo.entities;

import com.socialmedia.demo.enums.ReactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_reactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    
    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;
    
    private LocalDateTime createAt;
    
    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}