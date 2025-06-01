package com.socialmedia.demo.services;

import com.socialmedia.demo.entities.Chat;
import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.exceptions.ChatNotFoundException;
import com.socialmedia.demo.exceptions.UserNotFoundException;
import com.socialmedia.demo.mappers.ChatMapper;
import com.socialmedia.demo.repositories.ChatRepository;
import com.socialmedia.demo.repositories.UserRepository;
import com.socialmedia.demo.requests.Chat.ChatCreateRequest;
import com.socialmedia.demo.responses.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository; // Assuming UserRepository exists
    private final ChatMapper chatMapper;

    @Transactional
    public ChatResponse createChat(ChatCreateRequest request) {
        // Fetch member entities from the database
        List<User> members = userRepository.findAllById(request.getMemberIds());
        if (members.size() != request.getMemberIds().size()) {
            // Handle case where some users were not found if necessary
            // For simplicity, we'll proceed, but you might want to throw an exception
            // or filter out non-existent IDs depending on requirements.
            List<String> foundIds = members.stream().map(User::getId).collect(Collectors.toList());
            List<String> notFoundIds = request.getMemberIds().stream()
                                            .filter(id -> !foundIds.contains(id))
                                            .collect(Collectors.toList());
            // Consider logging or throwing an exception for notFoundIds
             System.out.println("Warning: Users not found for IDs: " + notFoundIds);
             if (members.isEmpty()) {
                 throw new UserNotFoundException("No valid members found for the chat.");
             }
        }

        // Check if a direct chat already exists between two users (if applicable)
        if (members.size() == 2) {
            Optional<Chat> existingChat = chatRepository.findDirectChatBetweenUsers(members.get(0).getId(), members.get(1).getId());
            if (existingChat.isPresent()) {
                // Return existing chat instead of creating a new one
                return chatMapper.toResponse(existingChat.get());
            }
        }


        Chat chat = new Chat(); // Create a new Chat entity
        chat.setMembers(members); // Set the fetched members

        Chat savedChat = chatRepository.save(chat);
        return chatMapper.toResponse(savedChat);
    }

    @Transactional(readOnly = true)
    public ChatResponse getChatById(String chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));
        return chatMapper.toResponse(chat);
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsByUserId(String userId) {
        // Ensure the user exists before fetching chats
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        List<Chat> chats = chatRepository.findByMembers_Id(userId);
        return chats.stream()
                .map(chatMapper::toResponse)
                .collect(Collectors.toList());
    }

     @Transactional(readOnly = true)
    public Optional<ChatResponse> findDirectChat(String userId1, String userId2) {
        // Ensure both users exist
        if (!userRepository.existsById(userId1)) {
            throw new UserNotFoundException("User not found with id: " + userId1);
        }
         if (!userRepository.existsById(userId2)) {
            throw new UserNotFoundException("User not found with id: " + userId2);
        }

        return chatRepository.findDirectChatBetweenUsers(userId1, userId2)
                             .map(chatMapper::toResponse);
    }


    // Optional: Add methods for adding/removing members, updating chat details, etc.
    // Example: Add member
    @Transactional
    public ChatResponse addMemberToChat(String chatId, String userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));
        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Avoid adding duplicate members
        if (!chat.getMembers().contains(userToAdd)) {
            chat.getMembers().add(userToAdd);
            chatRepository.save(chat);
        }
        return chatMapper.toResponse(chat);
    }

     // Example: Remove member
    @Transactional
    public ChatResponse removeMemberFromChat(String chatId, String userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (chat.getMembers().remove(userToRemove)) {
             // Consider deleting the chat if no members are left, depending on requirements
            if (chat.getMembers().isEmpty()) {
                 chatRepository.delete(chat);
                 // Or throw an exception / return a specific response
                 throw new IllegalStateException("Cannot remove the last member. Chat deleted instead.");
            } else {
                chatRepository.save(chat);
            }
        } else {
             // User wasn't a member, maybe log or throw an exception
             System.out.println("User " + userId + " was not a member of chat " + chatId);
        }
        // Return the updated chat or handle the case where the chat was deleted
        // For simplicity, returning the potentially modified chat (if not deleted)
        return chatMapper.toResponse(chat); // Be careful if chat might be deleted
    }


    @Transactional
    public void deleteChat(String chatId) {
        if (!chatRepository.existsById(chatId)) {
            throw new ChatNotFoundException("Chat not found with id: " + chatId);
        }
        // Add authorization checks here if needed (e.g., only members or admins can delete)
        chatRepository.deleteById(chatId);
    }
}