package com.runmate.service.user;

import com.runmate.domain.dto.AuthRequest;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService{
    @Autowired
    private UserRepository userRepository;

    public boolean join(User user) {
        if(user!=null && userRepository.findByEmail(user.getEmail())==null){
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean login(AuthRequest request) {
        User user=userRepository.findByEmail(request.getEmail());
        if(user==null || !user.getPassword().equals(request.getPassword()))
            return false;
        return true;
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }
    public void modify(String email,User modifiedUser){
        User user=userRepository.findByEmail(email);
        modifiedUser.setId(user.getId());
        userRepository.save(modifiedUser);
    }
    public boolean delete(String email){
        return userRepository.deleteByEmail(email)==1;
    }
}
