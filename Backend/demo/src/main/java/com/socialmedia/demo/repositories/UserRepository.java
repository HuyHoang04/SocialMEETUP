package com.socialmedia.demo.repositories;

import com.socialmedia.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Tìm kiếm User theo email (email là unique)
    Optional<User> findByEmail(String email);

    // Tìm kiếm User theo username (username là unique)
    Optional<User> findByUsername(String username);

    // Kiểm tra sự tồn tại của User theo email
    boolean existsByEmail(String email);

    // Kiểm tra sự tồn tại của User theo username
    boolean existsByUsername(String username);
}