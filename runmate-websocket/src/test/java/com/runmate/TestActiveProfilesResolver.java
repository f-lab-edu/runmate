package com.runmate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfilesResolver;

public class TestActiveProfilesResolver implements ActiveProfilesResolver {
    private static final Logger logger = LoggerFactory.getLogger(TestActiveProfilesResolver.class);

    @Override
    public String[] resolve(Class<?> testClass) {
        String profile = System.getProperty("profile");

        if (profile == null) {
            profile = "local";
            logger.warn("Default spring profiles active is null. use {} profile.", profile);
        }
        return new String[]{profile};
    }
}
