package com.runmate.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_device")
@Entity
public class UserDevice {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Id
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private User user;

    @Column(name = "device_alias")
    private String deviceAlias;

    @Column(name = "device_token")
    private String deviceToken;

    @Builder
    public UserDevice(User user, String deviceAlias, String deviceToken) {
        this.user = user;
        this.deviceAlias = deviceAlias;
        this.deviceToken = deviceToken;
    }
}
