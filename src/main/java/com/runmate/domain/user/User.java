package com.runmate.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="user")
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="email",nullable = false)
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="name")
    private String username;

    @Embedded
    private Region region;

    @Column(name="introduction")
    private String introduction;

    @Column(name="height")
    private int height;

    @Column(name="weight")
    private int weight;

    @Column(name="crew_role")
    @Enumerated(EnumType.STRING)
    private CrewRole crewRole;

    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
