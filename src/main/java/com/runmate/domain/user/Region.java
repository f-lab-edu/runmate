package com.runmate.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Region {
    @Column(name="zipcode")
    private String zipcode;
    @Column(name="address1")
    private String address1;
    @Column(name="address2")
    private String address2;
}
