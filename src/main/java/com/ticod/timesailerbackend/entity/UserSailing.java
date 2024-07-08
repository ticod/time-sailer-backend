package com.ticod.timesailerbackend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users_sailing",
    uniqueConstraints = @UniqueConstraint(
            name="UniqueUserAndSailing",
            columnNames = {"users_id", "sailing_id"}))
public class UserSailing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false, referencedColumnName = "id")
    private User users;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sailing_id", nullable = false)
    private Sailing sailing;

    @Builder
    public UserSailing(Integer id, User users, Sailing sailing) {
        this.id = id;
        this.users = users;
        this.sailing = sailing;
    }
}