package spring.btvnss12.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Định dạng JSON trả về cho client khi có lỗi.
 * Các field null sẽ được bỏ qua (ví dụ errors chỉ xuất hiện khi validate fail).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> errors
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, null);
    }

    public static ErrorResponse of(int status, String error, String message, String path,
                                   Map<String, String> errors) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, errors);
    }
}
