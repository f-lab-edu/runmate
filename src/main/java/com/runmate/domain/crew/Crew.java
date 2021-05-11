package com.runmate.domain.crew;

import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "crew")
public class Crew {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Embedded
    private Region region;

    @Column(name = "grade_limit")
    @Enumerated(EnumType.STRING)
    private Grade gradeLimit;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

}

