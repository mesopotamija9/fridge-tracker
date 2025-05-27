package dev.brkovic.fridge.tracker.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private final int code;
    private final Object data;

    public ApiException(HttpStatus status, String message, int code, Object data){
        super(message);
        this.status = status;
        this.message = message;
        this.code = code;
        this.data = data;
    }
}
