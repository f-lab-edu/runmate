package com.runmate.repository;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.activity.RunningTime;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.domain.dto.crew.CrewUserGetDto;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.crew.CrewUserQueryRepository;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.texture.TextureFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
public class CrewUserRepositoryTest {
    @Autowired
    CrewUserRepository crewUserRepository;

    @Autowired
    CrewUserQueryRepository crewUserQueryRepository;

    @Autowired
    TextureFactory textureFactory;

    @Autowired
    ActivityRepository activityRepository;

    @Test
    void When_SaveCrewUser_Expect_IncreasedCount() {
        final String email = "Lambda@Lambda.com";
        Crew crew = textureFactory.makeCrew(true);
        User user = textureFactory.makeUser(email, true);

        final int countBeforeSave = countOfCrewUser();
        CrewUser crewUser = textureFactory.makeCrewUser(crew, user, true);

        final int countAfterSave = countOfCrewUser();
        assertEquals(countBeforeSave + 1, countAfterSave);
    }

    @Test
    void When_DeleteCrewUser_Expect_DecreasedCount() {
        final String email = "Lambda@Lambda.com";
        Crew crew = textureFactory.makeCrew(true);
        User user = textureFactory.makeUser(email, true);

        CrewUser crewUser = textureFactory.makeCrewUser(crew, user, true);
        final int countBeforeDelete = countOfCrewUser();

        crewUserRepository.delete(crewUser);
        final int countAfterDelete = countOfCrewUser();

        assertEquals(countBeforeDelete - 1, countAfterDelete);
        assertNull(getCrewUser(crewUser));
    }

    @Test
    void When_FindCrewUser_Expect() {
        final String email = "Lambda@Lambda.com";
        Crew crew = textureFactory.makeCrew(true);
        User user = textureFactory.makeUser(email, true);
        CrewUser crewUser = textureFactory.makeCrewUser(crew, user, true);

        CrewUser result = crewUserRepository.findByCrewAndUser(crew, user).orElse(null);
        checkSameCrewUser(crewUser, result);
    }

    @Test
    void When_FindCrewUsersWithCrew_Expect() {
        final int numOfUser = 20;
        Crew crew = textureFactory.makeCrew(true);
        List<User> users = textureFactory.makeRandomUsers(numOfUser, true);

        for (User user : users) {
            textureFactory.makeCrewUser(crew, user, true);
        }

        List<CrewUser> crewUsers = crewUserRepository.findAllByCrew(crew);
        assertEquals(crewUsers.size(), numOfUser);
    }

    @Test
    void When_FindCrewUserOrderByActivity_Expect_SortedWithDistDesc() {
        //given
        final int numOfUser = 15;
        final int limit = 10;
        final int offset = 0;
        Crew crew = textureFactory.makeCrew(true);
        List<User> users = textureFactory.makeRandomUsers(numOfUser, true);
        for (User user : users) {
            CrewUser crewUser = textureFactory.makeCrewUser(crew, user, true);
        }

        List<Float> totalDistanceOrderByDesc = new ArrayList<>();
        for (int i = 0; i < numOfUser; i++) {
            final int numOfActivity = 5;

            float totalDistance = 0;
            for (int j = 0; j < numOfActivity; j++) {
                Activity activity = Activity.builder()
                        .runningTime(LocalTime.of(1, 30))
                        .distance((i + 1) * 3)
                        .calories(100)
                        .build();

                totalDistance += activity.getDistance();

                users.get(i).completeActivity(activity);
                activityRepository.save(activity);
            }
            totalDistanceOrderByDesc.add(totalDistance);
        }
        totalDistanceOrderByDesc.sort(Comparator.reverseOrder());

        //when
        List<CrewUserGetDto> result = crewUserQueryRepository.findCrewUserOrderByActivity(crew.getId(), offset, limit);

        //then
        assertEquals(limit, result.size());
        for (int i = 0; i < limit; i++) {
            assertEquals(totalDistanceOrderByDesc.get(i), result.get(i).getTotalDistance());
        }
    }

    int countOfCrewUser() {
        return crewUserRepository.findAll().size();
    }

    void checkSameCrewUser(CrewUser one, CrewUser another) {
        assertEquals(one.getCrew().getId(), another.getCrew().getId());
        assertEquals(one.getUser().getId(), another.getUser().getId());
        assertEquals(one.getRole(), another.getRole());
    }

    CrewUser getCrewUser(CrewUser crewUser) {
        return crewUserRepository.findById(crewUser.getId()).orElse(null);
    }
}
