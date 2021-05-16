package com.runmate.domain.user;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.common.LocalDateTimeConverter;
import com.runmate.domain.crew.CrewJoinRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Data
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Id
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 30)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "name", length = 20)
    private String username;

    @Embedded
    private Region region;

    @Column(name = "introduction", length = 255)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade")
    private Grade grade = Grade.UNRANKED;

    @Column(name = "created_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<Activity> activities = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private CrewJoinRequest crewJoinRequest;

    public boolean canUpgrade(float totalDistance) {
        return this.grade.canUpgrade(totalDistance);
    }

    public void upgrade() {
        this.grade = this.grade.getNext();
    }

    public void completeActivity(Activity activity) {
        this.activities.add(activity);
        activity.setUser(this);
    }
}
