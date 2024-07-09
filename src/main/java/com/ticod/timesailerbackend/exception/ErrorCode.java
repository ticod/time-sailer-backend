package com.ticod.timesailerbackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_DUPLICATED(HttpStatus.CONFLICT, "이미 가입된 회원");

    private final HttpStatus httpStatus;
    private final String message;

}
