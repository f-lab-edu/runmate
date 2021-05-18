package com.runmate.repository;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.crew.Role;
import com.runmate.domain.user.User;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.texture.TextureMaker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
public class CrewUserRepositoryTest {
    @Autowired
    CrewUserRepository crewUserRepository;

    @Autowired
    TextureMaker textureMaker;

    @Test
    void When_SaveCrewUser_Expect_IncreasedCount() {
        final String email = "Lambda@Lambda.com";
        Crew crew = textureMaker.makeCrew();
        User user = textureMaker.makeUser(email);

        final int countBeforeSave = countOfCrewUser();
        CrewUser crewUser = textureMaker.makeCrewUser(crew, user);

        final int countAfterSave = countOfCrewUser();
        assertEquals(countBeforeSave + 1, countAfterSave);
    }

    @Test
    void When_DeleteCrewUser_Expect_DecreasedCount() {
        final String email = "Lambda@Lambda.com";
        Crew crew = textureMaker.makeCrew();
        User user = textureMaker.makeUser(email);

        CrewUser crewUser = textureMaker.makeCrewUser(crew, user);
        final int countBeforeDelete = countOfCrewUser();

        crewUserRepository.delete(crewUser);
        final int countAfterDelete = countOfCrewUser();

        assertEquals(countBeforeDelete - 1, countAfterDelete);
        assertNull(getCrewUser(crewUser));
    }

    @Test
    void When_FindCrewUser_Expect() {
        final String email = "Lambda@Lambda.com";
        Crew crew = textureMaker.makeCrew();
        User user = textureMaker.makeUser(email);
        CrewUser crewUser = textureMaker.makeCrewUser(crew, user);

        CrewUser result=crewUserRepository.findByCrewAndUser(crew,user).orElse(null);
        checkSameCrewUser(crewUser,result);
    }

    @Test
    void When_FindCrewUsersWithCrew_Expect() {
        final int numOfUser=20;
        Crew crew=textureMaker.makeCrew();
        List<User> users=textureMaker.makeRandomUsers(numOfUser);

        for(User user:users){
            textureMaker.makeCrewUser(crew,user);
        }

        List<CrewUser>crewUsers=crewUserRepository.findAllByCrew(crew);
        assertEquals(crewUsers.size(),numOfUser);
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
