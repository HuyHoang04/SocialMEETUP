package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    // Tìm tất cả các reaction thuộc về một comment cụ thể
    List<CommentReaction> findByCommentId(String commentId);

    // Tìm tất cả các reaction được tạo bởi một người dùng cụ thể
    List<CommentReaction> findByUserId(String userId);

    // Tìm một reaction cụ thể bởi userId và commentId (hữu ích để kiểm tra xem người dùng đã reaction chưa)
    Optional<CommentReaction> findByUserIdAndCommentId(String userId, String commentId);

    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh khác ở đây nếu cần
}