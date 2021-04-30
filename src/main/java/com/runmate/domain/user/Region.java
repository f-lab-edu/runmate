package com.runmate.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {
    @Column(name="si", length = 20)
    private String si;
    @Column(name="gu", length = 20)
    private String gu;
    @Column(name="gun", length = 20)
    private String gun;
}
