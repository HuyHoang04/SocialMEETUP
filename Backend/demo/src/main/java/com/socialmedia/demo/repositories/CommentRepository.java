package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    // Tìm tất cả các comment thuộc về một bài đăng (Post) cụ thể
    List<Comment> findByPostId(String postId);

    // Tìm tất cả các comment được viết bởi một người dùng (User) cụ thể
    List<Comment> findByAuthorId(String authorId);

    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh khác ở đây nếu cần
    // Ví dụ: tìm comment theo author và post
    // Optional<Comment> findByAuthorIdAndPostId(String authorId, String postId);
}