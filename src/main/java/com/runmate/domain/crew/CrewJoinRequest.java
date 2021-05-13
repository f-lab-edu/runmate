package com.runmate.domain.crew;

import com.runmate.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "crew_join_request")
public class CrewJoinRequest {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Id
    private Long id;

    @JoinColumn(name = "id")
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private User user;

    @Setter
    @JoinColumn(name = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Crew crew;

    @CreatedDate
    private LocalDateTime createdAt;
}
