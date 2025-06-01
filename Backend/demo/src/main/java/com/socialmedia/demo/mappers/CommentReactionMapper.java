package com.socialmedia.demo.mappers;

import com.socialmedia.demo.entities.CommentReaction;
import com.socialmedia.demo.requests.CommentReaction.CommentReactionCreateRequest;
import com.socialmedia.demo.requests.CommentReaction.CommentReactionUpdateRequest;
import com.socialmedia.demo.responses.CommentReactionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class}) // Sử dụng UserMapper để map user
public interface CommentReactionMapper {

    @Mapping(target = "id", ignore = true) // Bỏ qua id vì nó được tạo tự động
    @Mapping(target = "user", ignore = true) // User sẽ được set thủ công trong service
    @Mapping(target = "comment", ignore = true) // Comment sẽ được set thủ công trong service
    @Mapping(target = "createAt", ignore = true) // createAt được set bởi @PrePersist
    CommentReaction toEntity(CommentReactionCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Chỉ cập nhật các trường không null
    @Mapping(target = "id", ignore = true) // Không cho phép cập nhật id
    @Mapping(target = "user", ignore = true) // Không cho phép cập nhật user qua mapper
    @Mapping(target = "comment", ignore = true) // Không cho phép cập nhật comment qua mapper
    @Mapping(target = "createAt", ignore = true) // Không cho phép cập nhật ngày tạo
    void updateEntityFromRequest(CommentReactionUpdateRequest request, @MappingTarget CommentReaction commentReaction);

    // Map từ CommentReaction entity sang CommentReactionResponse DTO
    // UserMapper sẽ tự động được sử dụng cho trường 'user'
    @Mapping(source = "comment.id", target = "commentId") // Map ID của Comment entity sang commentId trong response
    @Mapping(source = "id", target = "id", qualifiedByName = "longToString") // Map Long id sang String id
    CommentReactionResponse toResponse(CommentReaction commentReaction);

    // Phương thức tiện ích để cập nhật entity (tùy chọn)
    default CommentReaction updateEntity(CommentReaction commentReaction, CommentReactionUpdateRequest request) {
        updateEntityFromRequest(request, commentReaction);
        return commentReaction;
    }

    @Named("longToString")
    default String longToString(Long value) {
        return value != null ? String.valueOf(value) : null;
    }
}