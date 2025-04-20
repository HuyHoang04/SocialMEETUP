package com.socialmedia.demo.dtos.Chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUpdateRequest {
    private List<String> addMemberIds;
    private List<String> removeMemberIds;
}