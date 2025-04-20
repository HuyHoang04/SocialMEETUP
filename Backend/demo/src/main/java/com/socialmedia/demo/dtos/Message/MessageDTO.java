package com.socialmedia.demo.dtos.Message;

import com.socialmedia.demo.dtos.User.UserDTO;
import com.socialmedia.demo.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String id;
    private UserDTO sender;
    private String chatId;
    private LocalDateTime sendAt;
    private String content;
    private byte[] imageData;
    private String imageType;
    private MessageStatus messageStatus;
}