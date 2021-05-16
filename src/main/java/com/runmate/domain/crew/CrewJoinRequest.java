package com.runmate.domain.crew;

import com.runmate.domain.user.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "crew_join_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewJoinRequest {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Id
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private User user;

    @Setter
    @JoinColumn(name = "crew_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Crew crew;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public CrewJoinRequest(User user,Crew crew){
        this.user=user;
        this.crew=crew;
    }
}
