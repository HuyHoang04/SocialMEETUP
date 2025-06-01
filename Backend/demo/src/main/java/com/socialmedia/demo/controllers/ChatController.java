package com.socialmedia.demo.controllers;

import com.socialmedia.demo.requests.Chat.ChatCreateRequest;
import com.socialmedia.demo.responses.ApiResponse;
import com.socialmedia.demo.responses.ChatResponse;
import com.socialmedia.demo.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Đảm bảo đã import
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats") // Base path for chat-related endpoints
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Endpoint to create a new chat
    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> createChat(@RequestBody ChatCreateRequest request) {
        // Nên kiểm tra trong service xem request.getCreatorId() có khớp với người dùng đã xác thực không
        ChatResponse createdChat = chatService.createChat(request);
        ApiResponse<ChatResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.CREATED.value())); // 201
        response.setResult(createdChat);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint to get chat details by ID
    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponse<ChatResponse>> getChatById(@PathVariable String chatId) {
        // Kiểm tra quyền truy cập (user có phải là thành viên?) nên thực hiện trong service
        ChatResponse chat = chatService.getChatById(chatId);
        ApiResponse<ChatResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(chat);
        return ResponseEntity.ok(response);
    }

    // Endpoint to get all chats for a specific user
    // Sử dụng @PreAuthorize để kiểm tra quyền
    // 'authentication.name' lấy ID người dùng đã xác thực (vì ta cấu hình trả về ID)
    // '#userId' tham chiếu đến tham số userId của phương thức
    @PreAuthorize("authentication.name == #userId or hasRole('ADMIN')") // Cho phép user lấy chat của mình hoặc Admin lấy của bất kỳ ai
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getChatsByUserId(@PathVariable String userId) {
        List<ChatResponse> chats = chatService.getChatsByUserId(userId);
        ApiResponse<List<ChatResponse>> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(chats);
        return ResponseEntity.ok(response);
    }

    // Endpoint to add a member to a chat
    @PutMapping("/{chatId}/members/{userId}")
    public ResponseEntity<ApiResponse<ChatResponse>> addMemberToChat(
            @PathVariable String chatId,
            @PathVariable String userId) {
        // Kiểm tra quyền (ví dụ: chỉ admin/người tạo chat mới được thêm) nên thực hiện trong service
        ChatResponse updatedChat = chatService.addMemberToChat(chatId, userId);
        ApiResponse<ChatResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(updatedChat);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{chatId}/members/{userId}")
    public ResponseEntity<ApiResponse<ChatResponse>> removeMemberFromChat(
            @PathVariable String chatId,
            @PathVariable String userId) {
        ChatResponse updatedChat = chatService.removeMemberFromChat(chatId, userId);
        ApiResponse<ChatResponse> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200
        response.setResult(updatedChat);
        return ResponseEntity.ok(response);
    }

    // Endpoint to delete a chat
    @DeleteMapping("/{chatId}")
    public ResponseEntity<ApiResponse<String>> deleteChat(@PathVariable String chatId) {
        // Kiểm tra quyền (ví dụ: chỉ người tạo/admin) nên thực hiện trong service
        chatService.deleteChat(chatId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value())); // 200 or 204 No Content
        response.setResult("Chat deleted successfully with id: " + chatId);
        return ResponseEntity.ok(response); // Or ResponseEntity.noContent().build();
    }
}