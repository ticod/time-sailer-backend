package com.ticod.timesailerbackend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "password_salt", nullable = false, length = 100)
    private String passwordSalt;

    @Column(name = "refresh_token", nullable = false, length = 100)
    private String refreshToken;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Builder
    public User(Integer id, String email, String name, String password, String passwordSalt, String refreshToken, LocalDate createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.passwordSalt = passwordSalt;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
    }
}