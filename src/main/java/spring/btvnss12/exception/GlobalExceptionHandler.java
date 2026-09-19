package spring.btvnss12.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nơi bắt lỗi tập trung cho toàn bộ controller.
 * Mọi exception ném ra từ tầng controller/service đều đi qua đây và được
 * chuyển thành {@link ErrorResponse} dạng JSON.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /** Lỗi nghiệp vụ do mình chủ động throw. */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        log.warn("AppException: {} - {}", ex.getStatus(), ex.getMessage());
        return build(ex.getStatus(), ex.getMessage(), request);
    }

    /** @Valid trên @RequestBody thất bại. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors()
                .forEach(ge -> errors.putIfAbsent(ge.getObjectName(), ge.getDefaultMessage()));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.of(
                status.value(), status.getReasonPhrase(), "Dữ liệu không hợp lệ",
                request.getRequestURI(), errors));
    }

    /** @Validated trên @RequestParam / @PathVariable thất bại. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v ->
                errors.putIfAbsent(String.valueOf(v.getPropertyPath()), v.getMessage()));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.of(
                status.value(), status.getReasonPhrase(), "Dữ liệu không hợp lệ",
                request.getRequestURI(), errors));
    }

    /** Body JSON sai cú pháp hoặc sai kiểu dữ liệu. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Body request không đọc được hoặc sai định dạng JSON", request);
    }

    /** Thiếu query param bắt buộc. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                            HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Thiếu tham số bắt buộc: " + ex.getParameterName(), request);
    }

    /** Param sai kiểu, ví dụ /users/abc trong khi id là Long. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Tham số '%s' có giá trị không hợp lệ: %s".formatted(ex.getName(), ex.getValue()), request);
    }

    /** Sai HTTP method, ví dụ GET vào endpoint chỉ nhận POST. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                   HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method không được hỗ trợ: " + ex.getMethod(), request);
    }

    /** Không có endpoint tương ứng. */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex,
                                                         HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Không tìm thấy endpoint: " + request.getRequestURI(), request);
    }

    /** Vi phạm ràng buộc DB: unique, foreign key, not null... */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                             HttpServletRequest request) {
        log.warn("Vi phạm ràng buộc dữ liệu tại {}", request.getRequestURI(), ex);
        return build(HttpStatus.CONFLICT, "Dữ liệu vi phạm ràng buộc trong cơ sở dữ liệu", request);
    }

    /** Sai đối số khi gọi hàm, thường do logic kiểm tra đầu vào. */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleIllegal(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /** Lưới an toàn cuối cùng: mọi lỗi chưa được xử lý ở trên. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Lỗi không mong đợi tại {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Đã có lỗi xảy ra, vui lòng thử lại sau", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                message == null ? status.getReasonPhrase() : message,
                request.getRequestURI()));
    }
}
