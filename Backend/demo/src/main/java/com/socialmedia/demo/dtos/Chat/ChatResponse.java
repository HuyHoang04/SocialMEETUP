package com.socialmedia.demo.dtos.Chat;

import com.socialmedia.demo.dtos.Message.MessageDTO;
import com.socialmedia.demo.dtos.User.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String id;
    private List<UserDTO> members;
    private LocalDateTime createAt;
    private List<MessageDTO> messages;
    private MessageDTO lastMessage;
    private int unreadMessageCount;
    private int totalMessageCount;
    private boolean isGroupChat;
}