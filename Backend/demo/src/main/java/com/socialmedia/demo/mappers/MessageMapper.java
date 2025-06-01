package com.socialmedia.demo.mappers;

import com.socialmedia.demo.entities.Message;
import com.socialmedia.demo.requests.Message.MessageCreateRequest;
import com.socialmedia.demo.requests.Message.MessageUpdateRequest;
import com.socialmedia.demo.responses.MessageResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class}) // Sử dụng UserMapper để map sender
public interface MessageMapper {

    @Mapping(target = "id", ignore = true) // Bỏ qua id vì nó được tạo tự động
    @Mapping(target = "sender", ignore = true) // Sender sẽ được set thủ công trong service
    @Mapping(target = "chat", ignore = true) // Chat sẽ được set thủ công trong service dựa trên chatId từ request
    @Mapping(target = "sendAt", ignore = true) // sendAt được set bởi @PrePersist
    Message toEntity(MessageCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Chỉ cập nhật các trường không null
    @Mapping(target = "id", ignore = true) // Không cho phép cập nhật id
    @Mapping(target = "sender", ignore = true) // Không cho phép cập nhật sender qua mapper
    @Mapping(target = "chat", ignore = true) // Không cho phép cập nhật chat qua mapper
    @Mapping(target = "sendAt", ignore = true) // Không cho phép cập nhật ngày gửi
    void updateEntityFromRequest(MessageUpdateRequest request, @MappingTarget Message message);

    // Map từ Message entity sang MessageResponse DTO
    // UserMapper sẽ tự động được sử dụng cho trường 'sender'
    @Mapping(source = "chat.id", target = "chatId") // Map ID của Chat entity sang chatId trong response
    MessageResponse toResponse(Message message);

    // Phương thức tiện ích để cập nhật entity (tùy chọn)
    default Message updateEntity(Message message, MessageUpdateRequest request) {
        updateEntityFromRequest(request, message);
        // Logic cập nhật chat có thể được xử lý trong service nếu cần
        return message;
    }
}