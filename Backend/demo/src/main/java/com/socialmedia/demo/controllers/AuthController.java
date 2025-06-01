package com.socialmedia.demo.controllers;

// import com.socialmedia.demo.entities.User; // Không cần import User trực tiếp nữa
// import com.socialmedia.demo.exceptions.UserNotFoundException; // Không cần nữa
// import com.socialmedia.demo.repositories.UserRepository; // Không cần nữa
import com.socialmedia.demo.requests.Auth.LoginRequest;
import com.socialmedia.demo.responses.ApiResponse;
import com.socialmedia.demo.responses.JwtAuthenticationResponse;
import com.socialmedia.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder; // Không cần nữa
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    // private final UserRepository userRepository; // Không cần inject trực tiếp ở đây nữa
    // private final PasswordEncoder passwordEncoder; // Không cần inject trực tiếp ở đây nữa

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> authenticateUser(@RequestBody LoginRequest loginRequest) {

        try {
            // Sử dụng AuthenticationManager để xác thực
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Nếu xác thực thành công, thiết lập Authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo token từ Authentication object (Principal bây giờ là UserDetails)
            String jwt = tokenProvider.generateToken(authentication);

            ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>();
            response.setCode(String.valueOf(HttpStatus.OK.value()));
            response.setResult(new JwtAuthenticationResponse(jwt));
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            // Xử lý lỗi xác thực (sai username/email hoặc password)
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
            // Cung cấp thông báo lỗi chung chung hơn cho client
            errorResponse.setResult("Authentication failed: Invalid credentials.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
             // Xử lý các lỗi không mong muốn khác
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            errorResponse.setResult("An internal error occurred during authentication.");
             // Log lỗi chi tiết ở server
             // logger.error("Internal error during authentication", e);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}