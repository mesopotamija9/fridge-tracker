package dev.brkovic.fridge.tracker.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String VALIDATION_EXCEPTION_OCCURRED = "Validation exception occurred";

    @ExceptionHandler(value = {ApiException.class})
    protected ResponseEntity<ExceptionResponseDTO> handleApiException(ApiException apiException) {
        return ResponseEntity.status(apiException.getStatus()).body(createExceptionResponseDTO(apiException));
    }

    @ExceptionHandler(value = {InternalException.class})
    protected ResponseEntity<ExceptionResponseDTO> handleValidationException(InternalException internalException) {
        switch (internalException.getSeverityLevel()){
            case WARN -> log.warn(VALIDATION_EXCEPTION_OCCURRED, internalException);
            case ERROR -> log.error(VALIDATION_EXCEPTION_OCCURRED, internalException);
        }

        return ResponseEntity.status(internalException.getStatus()).body(createExceptionResponseDTO(internalException));
    }

    private ExceptionResponseDTO createExceptionResponseDTO(ApiException apiException){
        return ExceptionResponseDTO.builder()
                .message(apiException.getMessage())
                .code(apiException.getCode())
                .data(apiException.getData())
                .build();
    }
}
