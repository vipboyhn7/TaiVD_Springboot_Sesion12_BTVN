package spring.btvnss12.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    LocalDateTime timeStamp = LocalDateTime.now();
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(int status, String mess, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(mess)
                .data(data)
                .build();
    }
    public static <T> ApiResponse<T> success(int status, String mess) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(mess)
                .build();
    }

}
