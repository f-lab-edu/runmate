package com.runmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RunmateApplication {

    public static void main(String[] args) {
//        System.setProperty("spring.profiles.active", "stomp");
        SpringApplication.run(RunmateApplication.class, args);
    }

}
