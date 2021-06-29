package com.runmate;

import org.springframework.test.context.ActiveProfilesResolver;

public class TestActiveProfilesResolver implements ActiveProfilesResolver {

    @Override
    public String[] resolve(Class<?> testClass) {
        String profile = System.getProperty("profile");

        if (profile == null) {
            profile = "local";
        }
        return new String[]{profile};
    }
}