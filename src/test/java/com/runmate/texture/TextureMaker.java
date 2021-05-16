package com.runmate.texture;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewJoinRequestRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextureMaker {
    @Autowired
    private CrewJoinRequestRepository crewJoinRequestRepository;
    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private UserRepository userRepository;

    public Crew makeCrew(){
        Crew crew = Crew.builder()
                .name("run")
                .description("let's run")
                .region(new Region("MySi", "MyGu", null))
                .gradeLimit(Grade.UNRANKED)
                .build();
        crewRepository.save(crew);
        return crew;
    }

    public List<User> makeRandomUsers(int count){
        List<User>users=new ArrayList<>();
        for(int i=0; i<count; i++){
            users.add(makeUser("Lambda"+i));
        }
        return users;
    }

    public User makeUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("123");
        user.setIntroduction("i'm lambda");
        user.setRegion(new Region("MySi", "MyGu", null));
        userRepository.save(user);
        return user;
    }

    public CrewJoinRequest makeRequest(Crew crew, User user) {
        CrewJoinRequest request = CrewJoinRequest.builder()
                .user(user)
                .crew(crew)
                .build();
        crewJoinRequestRepository.save(request);
        return request;
    }
}
