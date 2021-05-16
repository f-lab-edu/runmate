package com.runmate.repository;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewJoinRequestRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CrewJoinRequestRepositoryTest {
    @Autowired
    CrewJoinRequestRepository crewJoinRequestRepository;

    @Autowired
    CrewRepository crewRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void When_RequestJoinToCrew_Expect_Save() {
        //given
        int countOfUser = 5;
        Crew crew = Crew.builder()
                .name("run")
                .description("let's run")
                .region(new Region("MySi", "MyGu", null))
                .gradeLimit(Grade.UNRANKED)
                .build();
        crewRepository.save(crew);

        List<User> users = new ArrayList<>();
        for (int i = 0; i < countOfUser; i++) {
            User user = new User();
            user.setEmail("lambda" + i);
            user.setPassword("123");
            user.setIntroduction("i'm lambda");
            user.setRegion(new Region("MySi", "MyGu", null));
            users.add(user);
        }

        int countBeforeSave=crewJoinRequestRepository.findAll().size();
        //when
        for (User user : users) {
            System.out.println(user.hashCode());
            userRepository.save(user);
            CrewJoinRequest request = CrewJoinRequest.builder()
                    .user(user)
                    .crew(crew)
                    .build();
            crewJoinRequestRepository.save(request);
        }

        int countAfterSave=crewJoinRequestRepository.findAll().size();
        //then
        assertEquals(countAfterSave, countBeforeSave+countOfUser);
    }
}
