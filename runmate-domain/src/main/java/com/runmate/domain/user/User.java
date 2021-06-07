package com.runmate.domain.user;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.common.LocalDateTimeConverter;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.crew.CrewUser;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
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

    @OneToOne(mappedBy = "user")
    private CrewUser crewUser;

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

    @OneToMany(mappedBy = "user")
    private List<CrewJoinRequest> joinRequests = new ArrayList<>();


    @Builder(builderMethodName = "of")
    public User(String email, String password, Region region, String introduction, String username) {
        this.email = email;
        this.password = password;
        this.region = region;
        this.introduction = introduction;
        this.username = username;
    }

    @Builder(builderMethodName = "ofGrade", builderClassName = "ForTest")
    public User(Grade grade, String email, String password, Region region, String introduction, String username) {
        this.email = email;
        this.password = password;
        this.region = region;
        this.introduction = introduction;
        this.username = username;
        this.grade = grade;
    }

    public boolean canUpgrade(float totalDistance) {
        return this.grade.canUpgrade(totalDistance);
    }

    public boolean isGradeHigherOrEqualThanCrewGradeLimit(Crew crew) {
        return this.getGrade().higherOrEqualThan(crew.getGradeLimit());
    }
    
    public void upgrade() {
        this.grade = this.grade.getNext();
    }

    public void completeActivity(Activity activity) {
        this.activities.add(activity);
        activity.setUser(this);
    }

    public boolean isRequestOfBelongingCrew(CrewJoinRequest request) {
        return crewUser.getCrew().getId().equals(request.getCrew().getId());
    }

    public boolean isMemberOfCrew(Long crewId) {
        if (crewUser == null) {
            return false;
        }
        return crewUser.getCrew().getId().equals(crewId);
    }

    public boolean isMatchedPassword(String password) {
        return this.password.equals(password);
    }
}
