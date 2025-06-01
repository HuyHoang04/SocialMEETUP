package com.socialmedia.demo.entities;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class User implements UserDetails  {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String username;
    private String fullname;
    @Column(nullable = false)
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
    @Column(nullable = false)
    private ROLE role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Post> posts;
    @ManyToMany(mappedBy = "members")
    private List<Chat> chats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserFriend> friendRelations;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserBlacklist> blacklistRelations;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về danh sách quyền hạn từ role
        return role.getAuthorities(); // Sử dụng phương thức getAuthorities() đã tạo trong enum ROLE
    }

    @Override
    public String getPassword() {
        return this.password; // Trả về mật khẩu đã mã hóa
    }

    @Override
    public String getUsername() {
        return this.username; // Trả về username (hoặc email nếu bạn muốn dùng email làm username chính)
    }

    // Các phương thức sau trả về true theo mặc định.
    // Bạn có thể thêm logic kiểm tra (ví dụ: cột is_active trong DB) nếu cần.
    @Override
    public boolean isAccountNonExpired() {
        return true; // Tài khoản không bao giờ hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Tài khoản không bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Thông tin đăng nhập (mật khẩu) không bao giờ hết hạn
    }

    @Override
    public boolean isEnabled() {
        return true; // Tài khoản được kích hoạt
    }
}
