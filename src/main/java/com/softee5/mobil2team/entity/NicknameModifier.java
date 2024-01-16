package com.softee5.mobil2team.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NicknameModifier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="created_datetime")
    private Date createdDatetime;

    @Column(name="updated_datetime")
    private Date updatedDatetime;

    @Column(name="modifier", nullable = false)
    private String modifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @PrePersist
    protected void onCreate() {
        createdDatetime = new Date();
        updatedDatetime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDatetime = new Date();
    }

}
