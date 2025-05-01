package com.socialmedia.demo.mappers;

import com.socialmedia.demo.entities.PostReaction;
import com.socialmedia.demo.requests.PostReaction.PostReactionCreateRequest;
import com.socialmedia.demo.requests.PostReaction.PostReactionUpdateRequest;
import com.socialmedia.demo.responses.PostReactionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class}) // Use UserMapper for user mapping
public interface PostReactionMapper {

    @Mapping(target = "id", ignore = true) // Ignore ID as it's auto-generated
    @Mapping(target = "user", ignore = true) // User will be set manually in the service
    @Mapping(target = "post", ignore = true) // Post will be set manually in the service
    @Mapping(target = "createAt", ignore = true) // createAt is set by @PrePersist
    PostReaction toEntity(PostReactionCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Update only non-null fields
    @Mapping(target = "id", ignore = true) // Do not update ID
    @Mapping(target = "user", ignore = true) // Do not update user directly via mapper
    @Mapping(target = "post", ignore = true) // Do not update post directly via mapper
    @Mapping(target = "createAt", ignore = true) // Do not update creation timestamp
    void updateEntityFromRequest(PostReactionUpdateRequest request, @MappingTarget PostReaction postReaction);

    // Map from PostReaction entity to PostReactionResponse DTO
    // UserMapper will be used automatically for the 'user' field mapping (PostReaction.user -> UserResponse)
    @Mapping(source = "post.id", target = "postId") // Map Post entity's ID to postId field in response
    PostReactionResponse toResponse(PostReaction postReaction);

    // Utility method (optional) to update entity - might not be needed if service handles update logic directly
    default PostReaction updateEntity(PostReaction postReaction, PostReactionUpdateRequest request) {
        updateEntityFromRequest(request, postReaction);
        return postReaction;
    }
}