package com.socialmedia.demo.mappers;

import com.socialmedia.demo.entities.Chat;
import com.socialmedia.demo.requests.Chat.ChatCreateRequest;
import com.socialmedia.demo.requests.Chat.ChatUpdateRequest;
import com.socialmedia.demo.responses.ChatResponse;
import org.mapstruct.*;

// Assuming MessageMapper exists or will be created
@Mapper(componentModel = "spring", uses = {UserMapper.class, MessageMapper.class})
public interface ChatMapper {

    @Mapping(target = "id", ignore = true) // Ignore ID as it's auto-generated
    @Mapping(target = "createAt", ignore = true) // Ignore createAt as it's set by @PrePersist
    @Mapping(target = "members", ignore = true) // Members will be handled in the service layer
    @Mapping(target = "messages", ignore = true) // Messages are not part of the creation request
    Chat toEntity(ChatCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // Do not update ID
    @Mapping(target = "createAt", ignore = true) // Do not update creation timestamp
    @Mapping(target = "members", ignore = true) // Member updates will be handled in the service layer
    @Mapping(target = "messages", ignore = true) // Message updates are handled separately
    void updateEntityFromRequest(ChatUpdateRequest request, @MappingTarget Chat chat);

    // MapStruct will automatically use UserMapper for members and MessageMapper for messages
    ChatResponse toResponse(Chat chat);

    // Optional helper method for update logic if needed in the service
    default Chat updateEntity(Chat chat, ChatUpdateRequest request) {
        updateEntityFromRequest(request, chat);
        // Additional logic for updating members might go here in the service layer
        return chat;
    }
}