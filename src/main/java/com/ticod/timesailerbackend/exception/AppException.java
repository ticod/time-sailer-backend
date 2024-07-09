package com.ticod.timesailerbackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

}
