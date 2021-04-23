package com.runmate.service;

import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    User user;
    @BeforeEach
    public void setUp(){
        userRepository.deleteAll();
        user=new User();
        user.setEmail("anny@anny.com");
        user.setPassword("1234");
        user.setWeight(123);
        user.setHeight(123);
        userService.join(user);
    }

    @Test
    public void getAndDelete(){
        User testUser=userService.getUser("anny@anny.com");

        assertEquals(testUser.getEmail(),user.getEmail());
        assertEquals(testUser.getPassword(),user.getPassword());
        assertEquals(testUser.getId(),user.getId());

        userService.delete(user.getEmail());
        assertNull(userService.getUser(user.getEmail()));
    }

    @Test
    public void modify(){
        User testUser=userRepository.findByEmail(user.getEmail());
        testUser.setPassword("12345");
        testUser.setWeight(321);
        userService.modify(testUser.getEmail(),testUser);

        User afterModified=userRepository.findByEmail(user.getEmail());
        assertEquals(user.getEmail(),afterModified.getEmail());
        assertEquals(user.getHeight(),afterModified.getHeight());

        assertNotEquals(user.getPassword(),afterModified.getPassword());
        assertNotEquals(user.getWeight(),afterModified.getWeight());
    }
}
