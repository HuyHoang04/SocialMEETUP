package com.socialmedia.demo.mappers;

import com.socialmedia.demo.entities.User;
import com.socialmedia.demo.requests.User.UserCreateRequest;
import com.socialmedia.demo.requests.User.UserUpdateRequest;
import com.socialmedia.demo.responses.UserResponse;
import org.mapstruct.*;

import java.util.Date;

@Mapper(componentModel = "spring", imports = Date.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true) // Bỏ qua id vì nó được tạo tự động
    @Mapping(target = "createdAt", expression = "java(new Date())") // Đặt ngày tạo là ngày hiện tại
    @Mapping(target = "posts", ignore = true) // Bỏ qua các mối quan hệ
    @Mapping(target = "chats", ignore = true)
    @Mapping(target = "friendRelations", ignore = true)
    @Mapping(target = "blacklistRelations", ignore = true)
    User toEntity(UserCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Chỉ cập nhật các trường không null
    @Mapping(target = "id", ignore = true) // Không cho phép cập nhật id
    @Mapping(target = "email", ignore = true) // Không cho phép cập nhật email
    @Mapping(target = "createdAt", ignore = true) // Không cho phép cập nhật ngày tạo
    @Mapping(target = "posts", ignore = true) // Bỏ qua các mối quan hệ
    @Mapping(target = "chats", ignore = true)
    @Mapping(target = "friendRelations", ignore = true)
    @Mapping(target = "blacklistRelations", ignore = true)
    void updateEntityFromRequest(UserUpdateRequest request, @MappingTarget User user);

    // Map từ User entity sang UserResponse DTO
    UserResponse toResponse(User user);

    // Phương thức tiện ích để cập nhật entity
    default User updateEntity(User user, UserUpdateRequest request) {
        updateEntityFromRequest(request, user);
        return user;
    }
}