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

    @Column(name="email",nullable = false,unique = true,length = 30)
    private String email;

    @Column(name="password",length = 255)
    private String password;

    @Column(name="name",length = 20)
    private String username;

    @Embedded
    private Region region;

    @Column(name="introduction",length = 255)
    private String introduction;

    @Convert(converter = GradeConverter.class)
    @Column(name="grade")
    Grade grade;

    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
