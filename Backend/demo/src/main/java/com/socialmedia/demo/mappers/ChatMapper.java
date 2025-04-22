package com.socialmedia.demo.mappers;

import com.socialmedia.demo.dtos.Chat.ChatDTO;
import com.socialmedia.demo.dtos.Chat.ChatUpdateRequest;
import com.socialmedia.demo.dtos.Message.MessageDTO;
import com.socialmedia.demo.entities.Chat;
import com.socialmedia.demo.entities.Message;
import com.socialmedia.demo.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatMapper {

    private final UserMapper userMapper;
    private final MessageMapper messageMapper;

    public ChatMapper(UserMapper userMapper, MessageMapper messageMapper) {
        this.userMapper = userMapper;
        this.messageMapper = messageMapper;
    }

    /**
     * Converts Chat entity to ChatDTO
     */
    public ChatDTO toDTO(Chat chat, User currentUser) {
        if (chat == null) {
            return null;
        }
        
        ChatDTO dto = new ChatDTO();
        dto.setId(chat.getId());
        dto.setCreateAt(chat.getCreateAt());
        
        // Map members
        if (chat.getMembers() != null) {
            dto.setMembers(chat.getMembers().stream()
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList()));
            
            // Determine if it's a group chat (more than 2 members)
            dto.setGroupChat(chat.getMembers().size() > 2);
        }
        
        // Map messages
        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            List<MessageDTO> messageDTOs = chat.getMessages().stream()
                    .map(messageMapper::toDTO)
                    .collect(Collectors.toList());
            
            dto.setMessages(messageDTOs);
            
            // Set last message
            Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);
            dto.setLastMessage(messageMapper.toDTO(lastMessage));
            
            // Count total messages
            dto.setTotalMessageCount(chat.getMessages().size());
            
            // Count unread messages for current user
            long unreadCount = chat.getMessages().stream()
                    .filter(message -> !message.getSender().getId().equals(currentUser.getId()))
                    .filter(message -> message.getMessageStatus() != null && 
                            !message.getMessageStatus().toString().equals("READ"))
                    .count();
            
            dto.setUnreadMessageCount((int) unreadCount);
        } else {
            dto.setMessages(new ArrayList<>());
            dto.setTotalMessageCount(0);
            dto.setUnreadMessageCount(0);
        }
        
        return dto;
    }
    
    /**
     * Creates a new Chat entity
     */
    public Chat toEntity(List<User> members) {
        if (members == null || members.isEmpty()) {
            return null;
        }
        
        Chat chat = new Chat();
        chat.setMembers(members);
        chat.setCreateAt(LocalDateTime.now());
        
        return chat;
    }
    
    /**
     * Updates chat members based on ChatUpdateRequest
     */
    public void updateChatMembers(ChatUpdateRequest request, Chat chat, List<User> addMembers, List<User> removeMembers) {
        if (request == null || chat == null) {
            return;
        }
        
        List<User> currentMembers = chat.getMembers();
        
        // Add new members
        if (addMembers != null && !addMembers.isEmpty()) {
            for (User member : addMembers) {
                if (!currentMembers.contains(member)) {
                    currentMembers.add(member);
                }
            }
        }
        
        // Remove members
        if (removeMembers != null && !removeMembers.isEmpty()) {
            currentMembers.removeAll(removeMembers);
        }
        
        chat.setMembers(currentMembers);
    }
}