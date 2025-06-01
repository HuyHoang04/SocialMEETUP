package com.socialmedia.demo.services;

import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // Đảm bảo transaction chỉ đọc
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Tìm kiếm user bằng username hoặc email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail)
                );
        // User entity đã implement UserDetails, nên có thể trả về trực tiếp
        return user;
    }

    // Optional: Phương thức để tải user bằng ID (dùng trong filter nếu cần)
    @Transactional(readOnly = true)
    public UserDetails loadUserById(String id) {
         User user = userRepository.findById(id).orElseThrow(
                 () -> new UsernameNotFoundException("User not found with id : " + id)
         );
         return user;
    }
}