package com.socialmedia.demo.entities;

import com.socialmedia.demo.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
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
    
    @Lob
    @Column(name = "image_data")
    private byte[] imageData;
    
    @Column(name = "image_type")
    private String imageType;
    
    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus;
    
    @PrePersist
    protected void onCreate() {
        sendAt = LocalDateTime.now();
        messageStatus = MessageStatus.SENT;
    }
}