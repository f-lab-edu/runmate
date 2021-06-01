package com.runmate.repository;

import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import com.runmate.texture.TextureFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TextureFactory textureFactory;

    @Test
    public void save() {
        final String email = "anny@anny.com";
        final int numOfUserBeforeSave = userRepository.findAll().size();

        User user = User.of()
                .email(email)
                .build();
        userRepository.save(user);

        final int numOfUserAfterSave = userRepository.findAll().size();
        assertEquals(numOfUserBeforeSave + 1, numOfUserAfterSave);
    }

    @Test
    public void find() {
        User user = userRepository.findByEmail("you@you.com");
        assertEquals(user.getEmail(), "you@you.com");
        assertEquals(user.getIntroduction(), "메일 뛰자!");
        assertEquals(user.getUsername(), "you");
        assertEquals(user.getPassword(), "1234");
    }
}
