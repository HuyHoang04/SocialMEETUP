package com.socialmedia.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Lob
    @Column(name = "image_data")
    private byte[] imageData;
    
    @Column(name = "image_type")
    private String imageType;
    
    private LocalDateTime createAt;
    
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentReaction> commentReactions;
    
    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}