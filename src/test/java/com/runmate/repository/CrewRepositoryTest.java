package com.runmate.repository;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.dto.crew.CrewGetDto;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.crew.CrewQueryRepository;
import com.runmate.repository.crew.CrewRepository;
import com.runmate.repository.spec.CrewOrderSpec;
import com.runmate.repository.user.UserRepository;
import com.runmate.texture.TextureFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CrewRepositoryTest {
    @Autowired
    CrewRepository crewRepository;
    @Autowired
    CrewQueryRepository crewQueryRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    TextureFactory textureFactory;

    @Test
    void When_SaveAndGet_Expect_AddedAndSameValue() {
        int countBeforeSave = countOfCrew();

        Crew crew = textureFactory.makeCrew(true);

        int countAfterSave = countOfCrew();
        assertEquals(countBeforeSave + 1, countAfterSave);

        Crew result = crewRepository.findById(crew.getId()).orElse(null);
        checkSameCrew(crew, result);
    }

    @Test
    void When_FindCrewsByLocation_OrderBy_Distance_Expect_OrderByDistance() {
        final int offSet = 0;
        final int limit = 10;
        final int numOfCrew = 11;
        List<Float> crewTotalDistances = new ArrayList<>();

        final Region myRegion = new Region("MySi", "MyGu", null);

        int count = 0;
        for (int i = 0; i < numOfCrew; i++) {
            final int numOfUser = 3;
            float crewTotalDistance = 0F;

            Crew crew = textureFactory.makeCrew(true);
            crew.setRegion(myRegion);

            for (int j = 0; j < numOfUser; j++) {
                final int numOfActivity = (i + 1);
                final String email = (count++) + "Lambda@Lambda.com";
                User user = textureFactory.makeUser(email, true);
                CrewUser crewUser = textureFactory.makeCrewUser(crew, user, true);

                for (int k = 0; k < numOfActivity; k++) {
                    Activity activity = Activity.builder()
                            .distance((i + 1) * 4F)
                            .runningTime(LocalTime.of(3, 30))
                            .calories(10)
                            .build();
                    user.completeActivity(activity);

                    crewTotalDistance += activity.getDistance();
                    activityRepository.save(activity);
                }
            }
            crewTotalDistances.add(crewTotalDistance);
        }

        //orderBy Distance Desc
        crewTotalDistances.sort(Comparator.reverseOrder());

        //when
        List<CrewGetDto> result = crewQueryRepository.findByLocationWithSorted(myRegion, PageRequest.of(offSet, limit),
                CrewOrderSpec.DESC_DISTANCE);

        //then
        assertEquals(limit, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(crewTotalDistances.get(i), result.get(i).getTotalDistance());
            System.out.println(result.get(i));
        }
    }


    @Test
    void When_FindCrewsByLocation_OrderByRunningTime_Expect_OrderByRunningTime() {
        final int offSet = 0;
        final int limit = 10;
        final int numOfCrew = 11;
        List<Long> crewTotalRunningTime = new ArrayList<>();

        final Region myRegion = new Region("MySi", "MyGu", null);

        int count = 0;
        for (int i = 0; i < numOfCrew; i++) {
            final int numOfUser = 3;
            long totalRunningTime = 0;

            Crew crew = textureFactory.makeCrew(true);
            crew.setRegion(myRegion);

            for (int j = 0; j < numOfUser; j++) {
                final int numOfActivity = (i + 1);
                final String email = (count++) + "Lambda@Lambda.com";
                User user = textureFactory.makeUser(email, true);
                CrewUser crewUser = textureFactory.makeCrewUser(crew, user, true);

                for (int k = 0; k < numOfActivity; k++) {
                    Activity activity = Activity.builder()
                            .distance(1F)
                            .runningTime(LocalTime.of((k+1), k))
                            .calories(10)
                            .build();
                    user.completeActivity(activity);

                    totalRunningTime += activity.getRunningTime().toSecondOfDay();
                    activityRepository.save(activity);
                }
            }
            crewTotalRunningTime.add(totalRunningTime);
        }

        //orderBy Distance Desc
        crewTotalRunningTime.sort(Comparator.reverseOrder());

        //when
        List<CrewGetDto> result = crewQueryRepository.findByLocationWithSorted(myRegion, PageRequest.of(offSet, limit),
                CrewOrderSpec.DESC_DISTANCE);

        //then
        assertEquals(limit, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(crewTotalRunningTime.get(i), result.get(i).getTotalRunningSeconds());
        }
    }

    void checkSameCrew(Crew expected, Crew result) {
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getCreatedAt(), result.getCreatedAt());
    }

    int countOfCrew() {
        return crewRepository.findAll().size();
    }
}
