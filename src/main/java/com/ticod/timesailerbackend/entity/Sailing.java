package com.ticod.timesailerbackend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "sailing")
public class Sailing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "sailing_uuid", nullable = false, length = 16, columnDefinition = "binary(16)")
    private String sailingUuid;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "is_shareable", nullable = false)
    private Boolean isShareable = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false, referencedColumnName = "id")
    private User users;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Builder
    public Sailing(Integer id, String sailingUuid, String title, LocalDate startDate, LocalDate dueDate, Boolean isShareable, User users, LocalDate createdAt) {
        this.id = id;
        this.sailingUuid = sailingUuid;
        this.title = title;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.isShareable = isShareable;
        this.users = users;
        this.createdAt = createdAt;
    }
}