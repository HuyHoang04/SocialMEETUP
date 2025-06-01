package com.socialmedia.demo.requests.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageUpdateRequest {
    private String id;
    private String senderId;
    private String chatId;
    private String content;
}