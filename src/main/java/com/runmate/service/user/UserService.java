package com.runmate.service.user;

import com.runmate.dto.AuthRequest;
import com.runmate.dto.user.UserModificationDto;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public boolean join(User user) {
        if (user != null && userRepository.findByEmail(user.getEmail()) == null) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !user.getPassword().equals(request.getPassword()))
            return false;
        return true;
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }

    public void modify(String email, UserModificationDto modificationDto) {
        User user = userRepository.findById(modificationDto.getId()).orElseThrow(IllegalArgumentException::new);
        modelMapper.map(modificationDto, user);
        userRepository.save(user);
    }

    public boolean delete(String email) {
        return userRepository.deleteByEmail(email) == 1;
    }
}
