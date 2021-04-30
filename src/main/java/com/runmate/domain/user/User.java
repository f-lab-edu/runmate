package com.runmate.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.runmate.domain.activity.Activity;
import com.runmate.domain.common.LocalDateTimeConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 30)
    @Email(message = "Field is not email format.")
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "name", length = 20)
    @NotNull(message="Field must be not null")
    private String username;

    @Embedded
    @NotNull(message = "Field must be not null")
    private Region region;

    @Column(name = "introduction", length = 255)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade")
    private Grade grade = Grade.UNRANKED;

    @Column(name = "created_at")
    @Convert(converter = LocalDateTimeConverter.class)
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Activity> activities = new ArrayList<>();

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
