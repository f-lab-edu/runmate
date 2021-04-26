package com.runmate.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Grade {
    RUBY('R',null,30000),
    DIA('D',RUBY,10000),
    PLATINUM('P',DIA,3000),
    GOLD('G',PLATINUM,1000),
    SILVER('S',GOLD,300),
    BRONZE('B',SILVER,50),
    UNRANKED('U',BRONZE,0);

    private char value;
    private Grade next;
    private int requiredScore;

    Grade(char value,Grade grade,int requiredScore){
        this.value=value;
        this.next=grade;
        this.requiredScore=requiredScore;
    }
    @JsonValue
    public char getValue(){
        return this.value;
    }
    public static Grade of(char value){
        return Arrays.stream(Grade.values())
                .filter(grade->grade.getValue()==value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
    @JsonCreator
    public static Grade fromChar(char symbol){
        return of(symbol);
    }
}
