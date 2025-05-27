package dev.brkovic.fridge.tracker.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponseDTO {
    private String message;
    private int code;
    private Object data;
}
