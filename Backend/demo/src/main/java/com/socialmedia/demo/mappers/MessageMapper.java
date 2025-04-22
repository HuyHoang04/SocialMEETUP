package com.socialmedia.demo.mappers;

import com.socialmedia.demo.dtos.Message.MessageCreateRequest;
import com.socialmedia.demo.dtos.Message.MessageDTO;
import com.socialmedia.demo.entities.Chat;
import com.socialmedia.demo.entities.Message;
import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.enums.MessageStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MessageMapper {

    private final UserMapper userMapper;

    public MessageMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Converts Message entity to MessageDTO
     */
    public MessageDTO toDTO(Message message) {
        if (message == null) {
            return null;
        }
        
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSender(userMapper.toDTO(message.getSender()));
        dto.setChatId(message.getChat().getId());
        dto.setSendAt(message.getSendAt());
        dto.setContent(message.getContent());
        dto.setImageData(message.getImageData());
        dto.setImageType(message.getImageType());
        dto.setMessageStatus(message.getMessageStatus());
        
        return dto;
    }
    
    /**
     * Creates a new Message entity from MessageCreateRequest
     */
    public Message toEntity(MessageCreateRequest request, User sender, Chat chat) {
        if (request == null) {
            return null;
        }
        
        Message message = new Message();
        message.setSender(sender);
        message.setChat(chat);
        message.setContent(request.getContent());
        message.setImageData(request.getImageData());
        message.setImageType(request.getImageType());
        message.setSendAt(LocalDateTime.now());
        message.setMessageStatus(MessageStatus.SENT);
        
        return message;
    }
    
    /**
     * Updates message status
     */
    public void updateMessageStatus(Message message, MessageStatus status) {
        if (message == null) {
            return;
        }
        
        message.setMessageStatus(status);
    }
}