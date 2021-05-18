package com.runmate.texture;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewJoinRequestRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextureFactory {
    @Autowired
    private CrewJoinRequestRepository crewJoinRequestRepository;
    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CrewUserRepository crewUserRepository;

    public Crew makeCrew(boolean doPersist) {
        Crew crew = Crew.builder()
                .name("run")
                .description("let's run")
                .region(new Region("MySi", "MyGu", null))
                .gradeLimit(Grade.UNRANKED)
                .build();
        if (doPersist)
            crewRepository.save(crew);
        return crew;
    }

    public List<User> makeRandomUsers(int count, boolean doPersist) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(makeUser("Lambda" + i, doPersist));
        }
        return users;
    }

    public User makeUser(String email, boolean doPersist) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("123");
        user.setIntroduction("i'm lambda");
        user.setRegion(new Region("MySi", "MyGu", null));
        user.setUsername("lambda");
        if (doPersist)
            userRepository.save(user);
        return user;
    }

    public CrewJoinRequest makeRequest(Crew crew, User user, boolean doPersist) {
        CrewJoinRequest request = CrewJoinRequest.builder()
                .user(user)
                .crew(crew)
                .build();
        if (doPersist)
            crewJoinRequestRepository.save(request);
        return request;
    }

    public CrewUser makeCrewUser(Crew crew, User user, boolean doPersist) {
        CrewUser crewUser = CrewUser.builder()
                .crew(crew)
                .user(user)
                .build();
        crewUser.setRole(Role.NORMAL);

        if (doPersist)
            crewUserRepository.save(crewUser);
        return crewUser;
    }
}
