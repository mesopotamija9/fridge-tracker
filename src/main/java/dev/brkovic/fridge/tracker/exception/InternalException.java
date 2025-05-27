package dev.brkovic.fridge.tracker.exception;

import dev.brkovic.fridge.tracker.enums.ResponseCode;
import dev.brkovic.fridge.tracker.enums.ExceptionSeverityLevel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalException extends ApiException {
    private final ExceptionSeverityLevel severityLevel;

    public InternalException(String message) {
        super(HttpStatus.OK, message, ResponseCode.EXPECTED_ERROR.getCode(), null);
        this.severityLevel = ExceptionSeverityLevel.WARN;
    }

    public InternalException(String message, HttpStatus httpStatus) {
        super(httpStatus, message, ResponseCode.EXPECTED_ERROR.getCode(), null);
        this.severityLevel = ExceptionSeverityLevel.WARN;
    }

    public InternalException(String message, ExceptionSeverityLevel severityLevel) {
        super(HttpStatus.OK, message, ResponseCode.EXPECTED_ERROR.getCode(), null);
        this.severityLevel = severityLevel;
    }
}
