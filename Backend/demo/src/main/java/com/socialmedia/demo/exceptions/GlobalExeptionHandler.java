package com.socialmedia.demo.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.socialmedia.demo.responses.ApiResponse;

@ControllerAdvice
public class GlobalExeptionHandler {

    // Handler cho lỗi không tìm thấy tài nguyên (ví dụ: UserNotFoundException)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setCode(String.valueOf(HttpStatus.NOT_FOUND.value())); // 404
        apiResponse.setResult(ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    // Handler cho lỗi vi phạm ràng buộc dữ liệu (ví dụ: email/username đã tồn tại)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setCode(String.valueOf(HttpStatus.CONFLICT.value())); // 409 hoặc BAD_REQUEST 400 tùy ngữ cảnh
        // Cung cấp thông báo lỗi chung chung hơn cho client
        apiResponse.setResult("Data integrity violation: " + ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    // Handler chung cho các RuntimeException khác (nên đặt cuối cùng)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse();
        // Giữ lại mã lỗi 500 cho các lỗi server không mong muốn
        apiResponse.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())); // 500
        apiResponse.setResult("An unexpected error occurred: " + exception.getMessage());
        // Trả về INTERNAL_SERVER_ERROR thay vì Bad Request cho lỗi server chung
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

}
