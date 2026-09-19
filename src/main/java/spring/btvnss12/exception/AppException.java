package spring.btvnss12.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exception dùng chung cho toàn bộ ứng dụng.
 * Cho phép throw lỗi "tự do": tự quyết định HTTP status + message ngay tại chỗ.
 *
 * <pre>
 * throw new AppException("Số dư không đủ");                          // 400
 * throw AppException.notFound("Không tìm thấy user id = " + id);     // 404
 * throw new AppException(HttpStatus.CONFLICT, "Email đã tồn tại");   // 409
 * </pre>
 */
@Getter
public class AppException extends RuntimeException {

    private final HttpStatus status;

    public AppException(String message) {
        this(HttpStatus.BAD_REQUEST, message);
    }

    public AppException(HttpStatus status, String message) {
        super(message);
        this.status = status == null ? HttpStatus.BAD_REQUEST : status;
    }

    public AppException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status == null ? HttpStatus.BAD_REQUEST : status;
    }

    // ----- Các shortcut hay dùng -----

    public static AppException badRequest(String message) {
        return new AppException(HttpStatus.BAD_REQUEST, message);
    }

    public static AppException notFound(String message) {
        return new AppException(HttpStatus.NOT_FOUND, message);
    }

    public static AppException conflict(String message) {
        return new AppException(HttpStatus.CONFLICT, message);
    }

    public static AppException unauthorized(String message) {
        return new AppException(HttpStatus.UNAUTHORIZED, message);
    }

    public static AppException forbidden(String message) {
        return new AppException(HttpStatus.FORBIDDEN, message);
    }

    public static AppException internal(String message) {
        return new AppException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
