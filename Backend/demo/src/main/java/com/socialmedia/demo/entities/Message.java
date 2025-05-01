package com.socialmedia.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    
    private LocalDateTime sendAt;
    
    @Column(columnDefinition = "TEXT")
    private String content;

    
    @PrePersist
    protected void onCreate() {
        sendAt = LocalDateTime.now();
    }
}