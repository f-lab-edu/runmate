package com.runmate.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;

public enum Grade {
    RUBY("RUBY", null, 30000),
    DIA("DIA", RUBY, 10000),
    PLATINUM("PLATINUM", DIA, 3000),
    GOLD("GOLD", PLATINUM, 1000),
    SILVER("SILVER", GOLD, 300),
    BRONZE("BRONZE", SILVER, 50),
    UNRANKED("UNRANKED", BRONZE, 0);

    private String  value;
    private Grade next;
    private int requiredScore;

    Grade(String value, Grade grade, int requiredScore) {
        this.value = value;
        this.next = grade;
        this.requiredScore = requiredScore;
    }

    public String getValue() {
        return this.value;
    }

    public static Grade of(String value) {
        return Arrays.stream(Grade.values())
                .filter(grade -> grade.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }


    public boolean canUpgrade(float totalDistance) {
        return totalDistance >= this.next.requiredScore;
    }

    @JsonIgnore
    public Grade getNext() {
        return this.next;
    }

    public boolean higherOrEqualThan(Grade grade){
        Grade copy=Grade.of(grade.getValue());
        while(copy!=null){
            if(copy==this)
                return true;
            copy=copy.next;
        }
        return false;
    }
}
