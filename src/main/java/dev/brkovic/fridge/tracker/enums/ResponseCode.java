package dev.brkovic.fridge.tracker.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    OK(0),
    VALIDATION_FAILED(1);

    private final int code;
}
