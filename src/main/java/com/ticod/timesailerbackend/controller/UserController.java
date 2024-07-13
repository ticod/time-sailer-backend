package com.ticod.timesailerbackend.controller;

import com.ticod.timesailerbackend.dto.Tokens;
import com.ticod.timesailerbackend.dto.UserJoinRequest;
import com.ticod.timesailerbackend.service.JwtAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final JwtAuthService jwtAuthService;

    @PostMapping
    public ResponseEntity<Tokens> join(@RequestBody UserJoinRequest request) {
        Tokens tokens = jwtAuthService.join(UserJoinRequest.toEntity(request));
        return ResponseEntity.ok().body(tokens);
    }

}
