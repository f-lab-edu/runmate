package com.runmate.websocket.domain;

import com.runmate.domain.crew.CrewUser;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "crew_user_id")
    @ManyToOne
    private CrewUser crewUser;

    @JoinColumn(name = "team_id")
    @ManyToOne
    private Team team;

    @Builder
    public TeamMember(CrewUser crewUser, Team team) {
        this.crewUser = crewUser;
        this.team = team;
    }
}
