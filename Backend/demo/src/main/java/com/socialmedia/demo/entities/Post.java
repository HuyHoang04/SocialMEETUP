package com.socialmedia.demo.entities;

import com.socialmedia.demo.enums.PrivacySetting;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Lob
    @Column(name = "image_data")
    private byte[] imageData;
    
    @Column(name = "image_type")
    private String imageType;
    
    private LocalDateTime createAt;
    
    @Enumerated(EnumType.STRING)
    private PrivacySetting privacySetting;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostReaction> postReactions;
    
    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}
