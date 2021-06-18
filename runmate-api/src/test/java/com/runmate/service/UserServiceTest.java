package com.runmate.service;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.domain.user.User;
import com.runmate.dto.user.UserModificationDto;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.runmate.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
public class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    static final String ADDRESS = "you@you.com";

    @Test
    @Transactional
    public void When_Modify_User_Expect_Remain_UserEmail_modify_UserName() {
        User user = userRepository.findByEmail(ADDRESS).orElseThrow(NotFoundUserEmailException::new);

        UserModificationDto modificationDto = UserModificationDto.builder()
                .username("modified username")
                .region(user.getRegion())
                .id(user.getId())
                .build();
        userService.modify(user.getEmail(),modificationDto);

        User modifiedUser=userRepository.findByEmail(user.getEmail()).orElseThrow(NotFoundUserEmailException::new);

        //should modified
        assertEquals(modifiedUser.getUsername(),modificationDto.getUsername());
        //should not modified
        assertEquals(modifiedUser.getEmail(),user.getEmail());
    }
}
