package com.socialmedia.demo.entities;

import java.util.Date;
import java.util.List;

import com.socialmedia.demo.enums.PrivacySetting;
import com.socialmedia.demo.enums.ROLE;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
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
    private String fullname;
    private String password;
    private String bio;
    private String gender;
    @Temporal(TemporalType.DATE)
    private Date dob;
    private Date createdAt;

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
    @Enumerated(EnumType.STRING)
    private ROLE role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Post> posts;
    @ManyToMany(mappedBy = "members")
    private List<Chat> chats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserFriend> friendRelations;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserBlacklist> blacklistRelations;
}
