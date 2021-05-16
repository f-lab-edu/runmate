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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    void When_Save_CrewJoinRequest_Expect_increasedSize() {
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

        int countBeforeSave = crewJoinRequestRepository.findAll().size();
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

        int countAfterSave = crewJoinRequestRepository.findAll().size();
        //then
        assertEquals(countAfterSave, countBeforeSave + countOfUser);
    }

    @Test
    void When_delete_CrewJoinRequest_Expect_SizeMinus1() {
        List<CrewJoinRequest> requests = crewJoinRequestRepository.findAll();
        crewJoinRequestRepository.delete(requests.get(0));
        assertEquals(requests.size() - 1, crewJoinRequestRepository.findAll().size());
    }

    @Test
    void When_Search_CrewJoinRequests_ByCrewId_Expect_OrderByCreatedAt() {
        List<CrewJoinRequest> expect = new ArrayList<>();

        Crew crew = Crew.builder()
                .name("run")
                .description("let's run")
                .region(new Region("MySi", "MyGu", null))
                .gradeLimit(Grade.UNRANKED)
                .build();
        crewRepository.save(crew);

        User user = new User();
        user.setEmail("lambda");
        user.setPassword("123");
        user.setIntroduction("i'm lambda");
        user.setUsername("lambda");
        user.setRegion(new Region("MySi", "MyGu", null));
        userRepository.save(user);

        for (int i = 0; i < 20; i++) {
            CrewJoinRequest request = CrewJoinRequest.builder()
                    .user(user)
                    .crew(crew)
                    .build();
            if (i % 2 == 0)
                request.setCreatedAt(LocalDateTime.of(2020, 12, 1 + i, 12, 30));
            else
                request.setCreatedAt(LocalDateTime.of(2020, 9, 20 - i, 12, 30));
            crewJoinRequestRepository.save(request);
            expect.add(request);
        }
        expect.sort(new Comparator<CrewJoinRequest>() {
            @Override
            public int compare(CrewJoinRequest o1, CrewJoinRequest o2) {
                if (o1.getCreatedAt().isAfter(o2.getCreatedAt()))
                    return -1;
                else if (o1.getCreatedAt().isEqual(o2.getCreatedAt()))
                    return 0;
                return 1;
            }
        });

        List<CrewJoinRequest> result = crewJoinRequestRepository.findAllByCrewWithPageable(crew,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")));

        assertEquals(result.size(), 10);

        for (int i = 0; i < result.size(); i++) {
            checkSameCrewJoinRequest(expect.get(i), result.get(i));
        }
    }

    void checkSameCrewJoinRequest(CrewJoinRequest one, CrewJoinRequest another) {
        assertEquals(one.getCrew().getId(), another.getCrew().getId());
        assertEquals(one.getUser().getId(), another.getUser().getId());
    }
}
