package com.runmate.domain.crew;

import com.runmate.domain.user.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "crew_join_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewJoinRequest {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Id
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private User user;

    @JoinColumn(name = "crew_id")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Crew crew;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public CrewJoinRequest(User user, Crew crew) {
        this.user = user;
        this.crew = crew;
    }

    public boolean isRequestForCrew(Long crewId) {
        return crew.getId().equals(crewId);
    }
}
