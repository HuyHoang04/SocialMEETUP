package com.socialmedia.demo.services;

import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.exceptions.UserNotFoundException;
import com.socialmedia.demo.mappers.UserMapper;
import com.socialmedia.demo.repositories.UserRepository;
import com.socialmedia.demo.requests.User.UserCreateRequest;
import com.socialmedia.demo.requests.User.UserUpdateRequest;
import com.socialmedia.demo.responses.UserResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Cần thiết cho việc mã hóa mật khẩu

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tự động tạo constructor với các final fields (Dependency Injection)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // Kiểm tra email và username đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists: " + request.getUsername());
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true) // Giao dịch chỉ đọc, tối ưu hiệu năng
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

     @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return userMapper.toResponse(user);
    }


    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        // Lưu ý: Nên xem xét sử dụng phân trang (Pagination) cho lượng dữ liệu lớn
    }

    @Transactional
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Kiểm tra nếu username trong request khác username hiện tại và đã tồn tại
        if (request.getUsername() != null && !request.getUsername().equals(existingUser.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
             throw new DataIntegrityViolationException("Username already exists: " + request.getUsername());
        }

        // Sử dụng mapper để cập nhật các trường từ request vào existingUser
        userMapper.updateEntityFromRequest(request, existingUser);

        // Xử lý cập nhật mật khẩu nếu có (cần mã hóa)
         if (request.getPassword() != null && !request.getPassword().isEmpty()) {
             existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}