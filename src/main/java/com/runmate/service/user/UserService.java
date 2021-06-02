package com.runmate.service.user;

import com.runmate.dto.AuthRequest;
import com.runmate.dto.user.UserModificationDto;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.exception.NotFoundUserEmailException;
import com.runmate.service.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public User join(User user) {
        userRepository.findByEmail(user.getEmail()).ifPresent(
                duplicate -> {
                    throw new IllegalArgumentException("duplicate email for " + duplicate.getEmail());
                }
        );

        return userRepository.save(user);
    }

    public User login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(NotFoundUserEmailException::new);
        if (!user.isMatchedPassword(request.getPassword())) {
            throw new UnAuthorizedException("email and password are mismatched");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(NotFoundUserEmailException::new);
    }

    public User modify(String email, UserModificationDto modificationDto) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserEmailException::new);
        modelMapper.map(modificationDto, user);
        return userRepository.save(user);
    }

    public boolean delete(String email) {
        return userRepository.deleteByEmail(email) == 1;
    }
}
