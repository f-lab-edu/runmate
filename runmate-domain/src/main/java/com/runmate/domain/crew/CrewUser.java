package com.runmate.domain.crew;

import com.runmate.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "crew_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewUser {

    @Setter
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

    @Setter
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public CrewUser(User user, Crew crew, Role role) {
        this.user = user;
        this.crew = crew;
        this.role = role;
    }

    public boolean isNormal() {
        return !role.equals(Role.ADMIN);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isEqualEmail(String email) {
        return user.getEmail().equals(email);
    }
}
