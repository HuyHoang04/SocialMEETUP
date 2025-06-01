package com.socialmedia.demo.controllers;

import com.socialmedia.demo.requests.Message.MessageCreateRequest;
import com.socialmedia.demo.requests.Message.MessageUpdateRequest;
import com.socialmedia.demo.responses.ApiResponse;
import com.socialmedia.demo.responses.MessageResponse;
import com.socialmedia.demo.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages") // Base path cho REST endpoints
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // --- REST Endpoints ---

    // Endpoint để lấy tin nhắn theo ID (chủ yếu để test hoặc truy xuất cụ thể)
    @GetMapping("/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageById(@PathVariable String messageId) {
        MessageResponse message = messageService.getMessageById(messageId);
        ApiResponse<MessageResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value()));
        response.setResult(message);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy tất cả tin nhắn trong một chat
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessagesByChatId(@PathVariable String chatId) {
        List<MessageResponse> messages = messageService.getMessagesByChatId(chatId);
        ApiResponse<List<MessageResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value()));
        response.setResult(messages);
        return ResponseEntity.ok(response);
    }

     // Endpoint để sửa tin nhắn (chỉ nội dung)
    @PutMapping("/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessage(
            @PathVariable String messageId,
            @RequestBody MessageUpdateRequest request) {
        // Lấy ID người dùng đã xác thực (cần cấu hình Spring Security)
        // String authenticatedUserId = getCurrentUserId(); // Implement this helper method
        // --- Giả sử bạn đã có cách lấy authenticatedUserId ---
        String authenticatedUserId = "some-authenticated-user-id"; // **PLACEHOLDER - THAY BẰNG LOGIC LẤY USER ID THỰC TẾ**

        MessageResponse updatedMessage = messageService.updateMessage(messageId, request, authenticatedUserId);
        ApiResponse<MessageResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value()));
        response.setResult(updatedMessage);
        return ResponseEntity.ok(response);
    }

    // Endpoint để xóa tin nhắn
    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<String>> deleteMessage(@PathVariable String messageId) {
         // Lấy ID người dùng đã xác thực
        // String authenticatedUserId = getCurrentUserId();
        // --- Giả sử bạn đã có cách lấy authenticatedUserId ---
        String authenticatedUserId = "some-authenticated-user-id"; // **PLACEHOLDER - THAY BẰNG LOGIC LẤY USER ID THỰC TẾ**

        messageService.deleteMessage(messageId, authenticatedUserId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value()));
        response.setResult("Message deleted successfully with id: " + messageId);
        return ResponseEntity.ok(response);
    }


    // --- WebSocket Message Handling ---

    /**
     * Xử lý tin nhắn gửi từ client đến một chat cụ thể.
     * Client sẽ gửi tin nhắn đến đích /app/chat/{chatId}
     * Tin nhắn sau khi xử lý sẽ được broadcast đến /topic/chats/{chatId}
     *
     * chatId ID của chat
     *  request Dữ liệu tin nhắn gửi từ client
     * return MessageResponse được broadcast cho các client khác
     */
    @MessageMapping("/chat/{chatId}") // Lắng nghe tin nhắn gửi đến /app/chat/{chatId}
    // @SendTo("/topic/chats/{chatId}") // Gửi kết quả trả về đến topic này (Service đã làm việc này rồi)
    public void handleChatMessage(@DestinationVariable String chatId, @Payload MessageCreateRequest request) {
         // Kiểm tra xem chatId trong path và trong payload có khớp không (tùy chọn)
         if (!chatId.equals(request.getChatId())) {
             // Xử lý lỗi hoặc bỏ qua
             System.err.println("Chat ID mismatch in WebSocket message!");
             return;
         }

        // Lấy senderId từ Principal hoặc SecurityContext nếu đã cấu hình xác thực WebSocket
        // String senderId = principal.getName(); // Ví dụ nếu dùng Principal
        // request.setSenderId(senderId);

        // Gọi service để tạo và lưu tin nhắn, service sẽ tự động broadcast
        messageService.createMessage(request);

        // Không cần return vì service đã dùng SimpMessagingTemplate để gửi
        // return messageResponse;
    }

    // --- Helper method to get current user ID (Cần implement dựa trên cơ chế xác thực của bạn) ---
    /*
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }
        // Giả sử Principal là UserDetails hoặc một object chứa ID
        // Ví dụ:
        // Object principal = authentication.getPrincipal();
        // if (principal instanceof UserDetails) {
        //     return ((UserDetails) principal).getUsername(); // Hoặc một phương thức khác để lấy ID
        // } else if (principal instanceof YourCustomPrincipal) {
        //     return ((YourCustomPrincipal) principal).getId();
        // }
        // return principal.toString(); // Hoặc cách khác tùy thuộc vào cấu hình Security
        throw new UnsupportedOperationException("getCurrentUserId() not implemented yet"); // Placeholder
    }
    */

}