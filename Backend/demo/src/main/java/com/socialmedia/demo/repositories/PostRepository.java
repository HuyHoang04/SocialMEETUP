package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.enums.PrivacySetting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh ở đây nếu cần
    // Ví dụ: Tìm tất cả các bài đăng của một người dùng cụ thể
    List<Post> findByAuthorId(String authorId);

    // Ví dụ: Tìm tất cả các bài đăng công khai
    List<Post> findByPrivacySetting(PrivacySetting privacySetting);
}