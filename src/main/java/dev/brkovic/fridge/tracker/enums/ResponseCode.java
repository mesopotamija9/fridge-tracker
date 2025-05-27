package dev.brkovic.fridge.tracker.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    OK(0),
    EXPECTED_ERROR(1);

    private final int code;
}
