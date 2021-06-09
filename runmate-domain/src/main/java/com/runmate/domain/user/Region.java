package com.runmate.domain.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {
    @Column(name = "si", length = 20)
    private String si;
    @Column(name = "gu", length = 20)
    private String gu;
    @Column(name = "gun", length = 20)
    private String gun;

    @Builder(builderMethodName = "of")
    public Region(String si, String gu, String gun) {
        this.si = si;
        this.gu = gu;
        this.gun = gun;
    }
}
