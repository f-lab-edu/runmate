package com.runmate.domain.crew;

import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "crew")
    private final List<CrewJoinRequest> joinRequests = new ArrayList<>();

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    public void addRequest(CrewJoinRequest request) {
        if (request == null) {
            throw new NullPointerException("크루 가입 요청이 비어있습니다.");
        }
        joinRequests.add(request);
        request.setCrew(this);
    }
}

