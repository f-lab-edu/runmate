package com.runmate.domain.user;

import java.util.Arrays;

public enum CrewRole {
    ADMIN("ADMIN"),NORMAL("NORMAL"),NO("NO");
    private String value;
    CrewRole(String value){
        this.value=value;
    }
    public String getValue(){
        return this.value;
    }

    public static CrewRole of(String role){
        System.out.println(role);
        return Arrays.stream(CrewRole.values())
                .filter(r->r.getValue().equals(role))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
