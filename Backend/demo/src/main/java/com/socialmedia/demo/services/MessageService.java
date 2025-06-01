package com.socialmedia.demo.services;

import com.socialmedia.demo.entities.Chat;
import com.socialmedia.demo.entities.Message;
import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.exceptions.ChatNotFoundException;
import com.socialmedia.demo.exceptions.MessageNotFoundException;
import com.socialmedia.demo.exceptions.UserNotFoundException;
import com.socialmedia.demo.mappers.MessageMapper;
import com.socialmedia.demo.repositories.ChatRepository;
import com.socialmedia.demo.repositories.MessageRepository;
import com.socialmedia.demo.repositories.UserRepository;
import com.socialmedia.demo.requests.Message.MessageCreateRequest;
import com.socialmedia.demo.requests.Message.MessageUpdateRequest;
import com.socialmedia.demo.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException; // Import nếu cần kiểm tra quyền
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate; // Để gửi tin nhắn qua WebSocket

    @Transactional
    public MessageResponse createMessage(MessageCreateRequest request) {
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new UserNotFoundException("Sender not found with id: " + request.getSenderId()));

        Chat chat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + request.getChatId()));

        // Kiểm tra xem người gửi có phải là thành viên của cuộc trò chuyện không (quan trọng)
        if (chat.getMembers().stream().noneMatch(member -> member.getId().equals(sender.getId()))) {
            throw new AccessDeniedException("Sender is not a member of this chat");
        }


        Message message = messageMapper.toEntity(request);
        message.setSender(sender);
        message.setChat(chat);

        Message savedMessage = messageRepository.save(message);
        MessageResponse messageResponse = messageMapper.toResponse(savedMessage);

        // Gửi tin nhắn mới tới tất cả client đang đăng ký topic của chat này
        messagingTemplate.convertAndSend("/topic/chats/" + chat.getId(), messageResponse);

        return messageResponse;
    }

    @Transactional(readOnly = true)
    public MessageResponse getMessageById(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + messageId));
        return messageMapper.toResponse(message);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessagesByChatId(String chatId) {
        // Kiểm tra xem chat có tồn tại không
        if (!chatRepository.existsById(chatId)) {
            throw new ChatNotFoundException("Chat not found with id: " + chatId);
        }
        List<Message> messages = messageRepository.findByChatIdOrderBySendAtAsc(chatId);
        return messages.stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    // Cần thêm tham số userId của người dùng đã xác thực để kiểm tra quyền
    public MessageResponse updateMessage(String messageId, MessageUpdateRequest request, String authenticatedUserId) {
        Message existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + messageId));

        // Kiểm tra xem người dùng có quyền sửa tin nhắn này không (chỉ người gửi mới được sửa)
        if (!existingMessage.getSender().getId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("User is not authorized to update this message");
        }

        // Chỉ cho phép cập nhật nội dung
        if (request.getContent() != null) {
             existingMessage.setContent(request.getContent());
        }
        // Không cho phép thay đổi senderId, chatId qua phương thức update này

        Message updatedMessage = messageRepository.save(existingMessage);
        MessageResponse messageResponse = messageMapper.toResponse(updatedMessage);

        // Gửi thông báo cập nhật tin nhắn qua WebSocket (tùy chọn)
        // Bạn có thể định nghĩa một loại message khác hoặc thêm trường vào MessageResponse để chỉ ra đây là update
        // messagingTemplate.convertAndSend("/topic/chats/" + existingMessage.getChat().getId() + "/updates", messageResponse);

        return messageResponse;
    }

    @Transactional
    // Cần thêm tham số userId của người dùng đã xác thực để kiểm tra quyền
    public void deleteMessage(String messageId, String authenticatedUserId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + messageId));

        // Kiểm tra xem người dùng có quyền xóa tin nhắn này không (chỉ người gửi mới được xóa)
        if (!message.getSender().getId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("User is not authorized to delete this message");
        }

        String chatId = message.getChat().getId(); // Lấy chatId trước khi xóa
        messageRepository.delete(message);

        // Gửi thông báo xóa tin nhắn qua WebSocket (tùy chọn)
        // Gửi ID của tin nhắn đã bị xóa
         messagingTemplate.convertAndSend("/topic/chats/" + chatId + "/deletions", messageId);
    }
}