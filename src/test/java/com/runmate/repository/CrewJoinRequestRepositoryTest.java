package com.runmate.repository;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewJoinRequestRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.texture.TextureMaker;
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
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
public class CrewJoinRequestRepositoryTest {
    @Autowired
    CrewJoinRequestRepository crewJoinRequestRepository;

    @Autowired
    CrewRepository crewRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TextureMaker textureMaker;

    @Test
    void When_Save_CrewJoinRequest_Expect_increasedCount() {
        //given
        final int numOfUser = 5;
        final int countBeforeSave = countOfCrewJoinRequest();

        Crew crew = textureMaker.makeCrew();
        for (int i = 0; i < numOfUser; i++) {
            final String email = "lambda" + i;
            User user = textureMaker.makeUser(email);
            textureMaker.makeRequest(crew, user);
        }

        final int countAfterSave = countOfCrewJoinRequest();

        //then
        assertEquals(countAfterSave, countBeforeSave + numOfUser);
    }

    @Test
    void When_delete_CrewJoinRequest_Expect_SizeMinus1() {
        Crew crew = textureMaker.makeCrew();
        User user = textureMaker.makeUser("scv");
        CrewJoinRequest request = textureMaker.makeRequest(crew, user);

        final int countBeforeDelete = countOfCrewJoinRequest();

        deleteCrewJoinRequest(request);

        final int countAfterDelete = countOfCrewJoinRequest();

        assertEquals(countBeforeDelete - 1, countAfterDelete);
        assertNull(getCrewJoinRequest(request.getId()));
    }

    @Test
    void When_Search_CrewJoinRequests_ByCrewId_Expect_OrderByCreatedAt() {
        //given
        final int numOfRequest = 20;
        List<CrewJoinRequest> expect = new ArrayList<>();

        Crew crew = textureMaker.makeCrew();

        for (int i = 0; i < numOfRequest; i++) {
            final String email = "lambda" + i;
            User user = textureMaker.makeUser(email);
            CrewJoinRequest request;

            if (i % 2 == 0) {
                request = makeRequestWithLocalDateTime(crew, user, LocalDateTime.of(2020, 12, 1 + i, 12, 30));
            } else {
                request = makeRequestWithLocalDateTime(crew, user, LocalDateTime.of(2020, 9, 20 - i, 12, 30));
            }
            expect.add(request);
        }
        configureOrderByCreatedAtDesc(expect);

        final int pageSize = 10;
        //when
        List<CrewJoinRequest> result = crewJoinRequestRepository.findAllByCrewWithPageable(crew,
                PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));

        //then
        assertEquals(result.size(), pageSize);
        //result same order with sorted CJR List
        for (int i = 0; i < result.size(); i++) {
            checkSameCrewJoinRequest(expect.get(i), result.get(i));
        }
    }

    @Test
    void When_findCrewJoinRequestByCrewAndUser_Expect_Return_SameUserAndCrew() {
        final int numOfUser = 10;
        final int testUser = 3;
        Crew crew = textureMaker.makeCrew();
        List<User> users = textureMaker.makeRandomUsers(numOfUser);

        for (User user : users) {
            textureMaker.makeRequest(crew, user);
        }

        CrewJoinRequest request = crewJoinRequestRepository.findCrewJoinRequestByCrewAndUser(crew, users.get(testUser));

        checkSameCrew(crew, request.getCrew());
        checkSameUser(users.get(testUser), request.getUser());
    }

    CrewJoinRequest makeRequestWithLocalDateTime(Crew crew, User user, LocalDateTime dateTime) {
        CrewJoinRequest request = CrewJoinRequest.builder()
                .user(user)
                .crew(crew)
                .build();
        request.setCreatedAt(dateTime);
        crewJoinRequestRepository.save(request);
        return request;
    }

    int countOfCrewJoinRequest() {
        return crewJoinRequestRepository.findAll().size();
    }

    void checkSameCrewJoinRequest(CrewJoinRequest one, CrewJoinRequest another) {
        assertEquals(one.getCrew().getId(), another.getCrew().getId());
        assertEquals(one.getUser().getId(), another.getUser().getId());
    }

    void deleteCrewJoinRequest(CrewJoinRequest request) {
        crewJoinRequestRepository.delete(request);
    }

    CrewJoinRequest getCrewJoinRequest(Long id) {
        return crewJoinRequestRepository.findById(id).orElse(null);
    }

    void configureOrderByCreatedAtDesc(List<CrewJoinRequest> requests) {
        requests.sort(new Comparator<CrewJoinRequest>() {
            @Override
            public int compare(CrewJoinRequest o1, CrewJoinRequest o2) {
                if (o1.getCreatedAt().isAfter(o2.getCreatedAt()))
                    return -1;
                else if (o1.getCreatedAt().isEqual(o2.getCreatedAt()))
                    return 0;
                return 1;
            }
        });
    }

    void checkSameCrew(Crew one, Crew another) {
        assertEquals(one.getId(), another.getId());
    }

    void checkSameUser(User one, User another) {
        assertEquals(one.getId(), another.getId());
    }
}
