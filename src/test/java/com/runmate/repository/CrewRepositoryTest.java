package com.runmate.repository;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.user.Grade;
import com.runmate.domain.user.Region;
import com.runmate.repository.crew.CrewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CrewRepositoryTest {
    @Autowired
    CrewRepository crewRepository;

    @Test
    void When_SaveAndGet_Expect_AddedAndSameValue() {
        Crew crew = Crew.builder()
                .name("같이 뛸래?")
                .description("함께 뜁시다")
                .gradeLimit(Grade.SILVER)
                .region(new Region("si123", "gu123", null))
                .build();

        int countBeforeSave =crewRepository.findAll().size();
        crewRepository.save(crew);

        int countAfterSave=crewRepository.findAll().size();
        assertEquals(countBeforeSave +1,countAfterSave);

        Crew result=crewRepository.findById(crew.getId()).orElse(null);
        checkSameCrew(crew,result);
    }

    void checkSameCrew(Crew expected,Crew result){
        assertEquals(expected.getId(),result.getId());
        assertEquals(expected.getDescription(),result.getDescription());
        assertEquals(expected.getName(),result.getDescription());
        assertEquals(expected.getCreatedAt(),result.getCreatedAt());
    }
}
