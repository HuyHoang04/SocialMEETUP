package com.socialmedia.demo.mappers;

import com.socialmedia.demo.entities.Comment;
import com.socialmedia.demo.requests.Comment.CommentCreateRequest;
import com.socialmedia.demo.requests.Comment.CommentUpdateRequest;
import com.socialmedia.demo.responses.CommentResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class}) // Sử dụng UserMapper để map author
public interface CommentMapper {

    @Mapping(target = "id", ignore = true) // Bỏ qua id vì nó được tạo tự động
    @Mapping(target = "author", ignore = true) // Author sẽ được set thủ công trong service
    @Mapping(target = "post", ignore = true) // Post sẽ được set thủ công trong service
    @Mapping(target = "createAt", ignore = true) // createAt được set bởi @PrePersist
    @Mapping(target = "commentReactions", ignore = true) // Bỏ qua các mối quan hệ khác
    Comment toEntity(CommentCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Chỉ cập nhật các trường không null
    @Mapping(target = "id", ignore = true) // Không cho phép cập nhật id
    @Mapping(target = "author", ignore = true) // Không cho phép cập nhật author qua mapper
    @Mapping(target = "post", ignore = true) // Không cho phép cập nhật post qua mapper
    @Mapping(target = "createAt", ignore = true) // Không cho phép cập nhật ngày tạo
    @Mapping(target = "commentReactions", ignore = true) // Bỏ qua các mối quan hệ khác
    void updateEntityFromRequest(CommentUpdateRequest request, @MappingTarget Comment comment);

    // Map từ Comment entity sang CommentResponse DTO
    // UserMapper sẽ tự động được sử dụng cho trường 'author'
    @Mapping(source = "post.id", target = "postId") // Map ID của Post entity sang postId trong response
    CommentResponse toResponse(Comment comment);

    // Phương thức tiện ích để cập nhật entity (tùy chọn)
    default Comment updateEntity(Comment comment, CommentUpdateRequest request) {
        updateEntityFromRequest(request, comment);
        return comment;
    }
}