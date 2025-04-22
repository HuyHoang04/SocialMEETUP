package com.socialmedia.demo.mappers;

import com.socialmedia.demo.dtos.User.UserDTO;
import com.socialmedia.demo.dtos.User.UserCreateRequest;
import com.socialmedia.demo.dtos.User.UserUpdateRequest;
import com.socialmedia.demo.dtos.User.ProfilePictureUpdateRequest;
import com.socialmedia.demo.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Converts User entity to UserDTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setBio(user.getBio());
        dto.setGender(user.getGender());
        dto.setDob(user.getDob());
        dto.setPrivacySetting(user.getPrivacySetting());
        dto.setAvatar(user.getAvatar());
        dto.setAvatarType(user.getAvatarType());
        dto.setCoverPicture(user.getCoverPicture());
        dto.setCoverPictureType(user.getCoverPictureType());
        dto.setRole(user.getRole());
        
        return dto;
    }
    
    /**
     * Creates a new User entity from UserCreateRequest
     */
    public User toEntity(UserCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setBio(request.getBio());
        user.setGender(request.getGender());
        user.setDob(request.getDob());
        user.setPrivacySetting(request.getPrivacySetting());
        
        return user;
    }
    
    /**
     * Updates an existing User entity from UserUpdateRequest
     */
    public void updateEntityFromDto(UserUpdateRequest request, User user) {
        if (request == null || user == null) {
            return;
        }
        
        user.setBio(request.getBio());
        user.setGender(request.getGender());
        user.setDob(request.getDob());
        user.setPrivacySetting(request.getPrivacySetting());
    }
    
    /**
     * Updates profile picture (avatar or cover) from ProfilePictureUpdateRequest
     */
    public void updateProfilePicture(ProfilePictureUpdateRequest request, User user) {
        if (request == null || user == null) {
            return;
        }
        
        if (request.isAvatar()) {
            user.setAvatar(request.getImageData());
            user.setAvatarType(request.getImageType());
        } else {
            user.setCoverPicture(request.getImageData());
            user.setCoverPictureType(request.getImageType());
        }
    }
    
    /**
     * Updates user password
     */
    public void updatePassword(User user, String newPassword) {
        if (user == null || newPassword == null) {
            return;
        }
        
        user.setPassword(newPassword);
    }
}