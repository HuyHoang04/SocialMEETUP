package com.socialmedia.demo.security;

import com.socialmedia.demo.entities.User; // Giả sử bạn có User entity
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    // Nên lưu secret key và expiration trong application.properties/yml
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
         if (jwtSecret == null || jwtSecret.length() < 32) {
             logger.warn("JWT Secret is not configured or too short. Using a default insecure key. PLEASE CONFIGURE a strong secret in properties!");
             // **KHÔNG SỬ DỤNG KEY NÀY TRONG PRODUCTION**
             return Keys.secretKeyFor(SignatureAlgorithm.HS256); // Tạo key ngẫu nhiên (không ổn định)
         }
         // Chuyển đổi chuỗi secret thành key (cần đảm bảo chuỗi đủ mạnh)
         return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Tạo JWT từ thông tin User (hoặc Authentication)
    public String generateToken(Authentication authentication) {
        // Lấy thông tin user từ Authentication object
        Object principal = authentication.getPrincipal();
        String userId;

        if (principal instanceof User) {
            userId = ((User) principal).getId(); // Lấy ID từ User object
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Fallback nếu principal là UserDetails nhưng không phải User entity của bạn
            // Cần đảm bảo UserDetails chứa ID hoặc có cách lấy ID
            // Ví dụ: Lấy username và tìm user trong DB (không khuyến khích vì tốn hiệu năng)
            // Hoặc ném lỗi nếu cấu hình không đúng
             throw new IllegalStateException("Principal is not an instance of User entity. Cannot extract User ID.");
            // userId = authentication.getName(); // Giữ nguyên nếu không lấy được ID
        } else {
             throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }


        String role = authentication.getAuthorities().stream()
                                   .map(GrantedAuthority::getAuthority)
                                   .collect(Collectors.joining(",")); // Lấy role

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userId) // Đặt userId làm subject
                .claim("role", role) // Thêm role vào claims
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sử dụng HS256 hoặc thuật toán mạnh hơn
                .compact();
    }

     // Tạo JWT trực tiếp từ User entity (ví dụ sau khi đăng nhập thành công)
     public String generateToken(User user) {
         Date now = new Date();
         Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

         return Jwts.builder()
                 .setSubject(user.getId()) // Sử dụng ID của User
                 .claim("role", user.getRole().name()) // Lấy role từ User entity
                 .setIssuedAt(now)
                 .setExpiration(expiryDate)
                 .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                 .compact();
     }


    // Lấy User ID từ JWT
    public String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Lấy Role từ JWT
    public String getRoleFromJWT(String token) {
         Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        // Lấy claim 'role', đảm bảo xử lý null hoặc kiểu dữ liệu không đúng
        Object roleClaim = claims.get("role");
        return roleClaim instanceof String ? (String) roleClaim : null;
    }


    // Xác thực JWT
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}