package com.ticod.timesailerbackend.dto;

import com.ticod.timesailerbackend.entity.User;

import java.time.LocalDate;

public record UserJoinRequest(String email, String name, String password) {

    public static User toEntity(UserJoinRequest userJoinRequest) {
        return User.builder()
                .email(userJoinRequest.email)
                .name(userJoinRequest.name)
                .password(userJoinRequest.password)
                .createdAt(LocalDate.now())
                .build();
    }

}
