package com.runmate.repository;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewQueryRepository;
import com.runmate.repository.crew.CrewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CrewRepositoryTest {
    @Autowired
    CrewRepository crewRepository;
    @Autowired
    CrewQueryRepository crewQueryRepository;

    @Test
    void When_SaveAndGet_Expect_AddedAndSameValue() {
        Crew crew = Crew.builder()
                .name("같이 뛸래?")
                .description("함께 뜁시다")
                .gradeLimit(Grade.SILVER)
                .region(new Region("si123", "gu123", null))
                .build();

        int countBeforeSave = crewRepository.findAll().size();
        crewRepository.save(crew);

        int countAfterSave = crewRepository.findAll().size();
        assertEquals(countBeforeSave + 1, countAfterSave);

        Crew result = crewRepository.findById(crew.getId()).orElse(null);
        checkSameCrew(crew, result);
    }

    @Test
    void When_FindCrewsByLocation_WithSameRegion_Expect_10CrewData() {
        //when
        List<Crew> savedCrews = new ArrayList<>();
        Region myRegion = new Region("MySi", "MyGu", null);
        for (int i = 0; i < 10; i++) {
            Crew crew = Crew.builder()
                    .name("같이 뛸래?" + i)
                    .description("함께 뜁시다" + i)
                    .gradeLimit(Grade.SILVER)
                    .region(myRegion)
                    .build();

            savedCrews.add(crew);
            crewRepository.save(crew);
        }
        List<Crew> actualCrews = crewQueryRepository.findByLocationWithSorted(myRegion);

        assertEquals(savedCrews.size(), actualCrews.size());
        for (int i = 0; i < savedCrews.size(); i++) {
            checkSameCrew(savedCrews.get(i), actualCrews.get(i));
        }
    }

    void checkSameCrew(Crew expected, Crew result) {
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getCreatedAt(), result.getCreatedAt());
    }
}
