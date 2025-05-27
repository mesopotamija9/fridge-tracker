package dev.brkovic.fridge.tracker.exception;

import dev.brkovic.fridge.tracker.enums.ResponseCode;
import dev.brkovic.fridge.tracker.enums.ExceptionSeverityLevel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends ApiException {
    private final ExceptionSeverityLevel severityLevel;

    public ValidationException(String message) {
        super(HttpStatus.OK, message, ResponseCode.VALIDATION_FAILED.getCode(), null);
        this.severityLevel = ExceptionSeverityLevel.WARN;
    }

    public ValidationException(String message, HttpStatus httpStatus) {
        super(httpStatus, message, ResponseCode.VALIDATION_FAILED.getCode(), null);
        this.severityLevel = ExceptionSeverityLevel.WARN;
    }

    public ValidationException(String message, ExceptionSeverityLevel severityLevel) {
        super(HttpStatus.OK, message, ResponseCode.VALIDATION_FAILED.getCode(), null);
        this.severityLevel = severityLevel;
    }
}
