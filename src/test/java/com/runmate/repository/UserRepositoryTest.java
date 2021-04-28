package com.runmate.repository;

import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
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

    @Test
    public void save(){
        User user=new User();
        user.setEmail("anny@anny.com");
        user.setPassword("1234");
        user.setUsername("yousung");
        user.setRegion(new Region("seoul","nowon",null));
        user.setIntroduction("my name is yousung");

        userRepository.save(user);
        assertEquals(userRepository.findAll().size(),4);
    }
    @Test
    public void find(){
        User user=userRepository.findByEmail("you@you.com");
        assertEquals(user.getEmail(),"you@you.com");
        assertEquals(user.getIntroduction(),"메일 뛰자!");
        assertEquals(user.getUsername(),"you");
        assertEquals(user.getPassword(),"1234");
    }
}
