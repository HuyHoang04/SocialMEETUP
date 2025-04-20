package com.socialmedia.demo.dtos.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreateRequest {
    private String chatId;
    private String content;
    private byte[] imageData;
    private String imageType;
}