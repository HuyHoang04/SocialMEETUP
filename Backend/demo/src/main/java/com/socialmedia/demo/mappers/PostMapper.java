package com.socialmedia.demo.mappers;

import com.socialmedia.demo.entities.Post;
import com.socialmedia.demo.requests.Post.PostCreateRequest;
import com.socialmedia.demo.requests.Post.PostUpdateRequest;
import com.socialmedia.demo.responses.PostResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = UserMapper.class) // Use UserMapper for author mapping
public interface PostMapper {

    @Mapping(target = "id", ignore = true) // Ignore ID as it's auto-generated
    @Mapping(target = "author", ignore = true) // Author will be set manually in the service
    @Mapping(target = "createAt", ignore = true) // createAt is set by @PrePersist
    @Mapping(target = "comments", ignore = true) // Ignore relationships
    @Mapping(target = "postReactions", ignore = true) // Ignore relationships
    Post toEntity(PostCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Update only non-null fields
    @Mapping(target = "id", ignore = true) // Do not update ID
    @Mapping(target = "author", ignore = true) // Do not update author directly via mapper
    @Mapping(target = "createAt", ignore = true) // Do not update creation timestamp
    @Mapping(target = "comments", ignore = true) // Ignore relationships
    @Mapping(target = "postReactions", ignore = true) // Ignore relationships
    void updateEntityFromRequest(PostUpdateRequest request, @MappingTarget Post post);

    // Map from Post entity to PostResponse DTO
    // UserMapper will be used automatically for the 'author' field mapping (Post.author -> UserResponse)
    PostResponse toResponse(Post post);

    // Utility method to update entity
    default Post updateEntity(Post post, PostUpdateRequest request) {
        updateEntityFromRequest(request, post);
        return post;
    }
}