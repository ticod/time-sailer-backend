package com.ticod.timesailerbackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 401 Error
    USER_EMAIL_NOT_FOUND(HttpStatus.UNAUTHORIZED, "가입되지 않은 회원"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호"),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 엑세스 토큰"),
    // 409 Error
    USER_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 가입된 회원"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
