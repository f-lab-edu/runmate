package com.runmate.domain.crew;

import com.runmate.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "crew_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewUser {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Id
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private User user;

    @JoinColumn(name = "crew_id")
    @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private Crew crew;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public CrewUser(User user,Crew crew){
        this.user=user;
        this.crew=crew;
    }
}
