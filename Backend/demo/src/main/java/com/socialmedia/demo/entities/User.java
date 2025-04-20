package com.socialmedia.demo.entities;

import java.util.Date;
import java.util.List;

import com.socialmedia.demo.enums.PrivacySetting;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;

    private String password;
    private String bio;
    private String gender;
    @Temporal(TemporalType.DATE)
    private Date dob;

    @Enumerated(EnumType.STRING)
    private PrivacySetting privacySetting;

    @Lob
    @Column(name = "avatar_data")
    private byte[] avatar;
    
    @Column(name = "avatar_type")
    private String avatarType;
    
    @Lob
    @Column(name = "cover_picture_data")
    private byte[] coverPicture;
    
    @Column(name = "cover_picture_type")
    private String coverPictureType;
    
    private String role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Post> posts;
    @ManyToMany(mappedBy = "members")
    private List<Chat> chats;
    
    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;
    
    @ManyToMany
    @JoinTable(
        name = "user_blacklists",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "blacklisted_id")
    )
    private List<User> blacklists;
}
