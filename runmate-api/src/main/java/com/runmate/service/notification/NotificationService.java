package com.runmate.service.notification;

import com.runmate.domain.user.User;
import com.runmate.domain.user.UserDevice;
import com.runmate.exception.NotFoundUserEmailException;
import com.runmate.repository.user.UserDeviceRepository;
import com.runmate.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final UserRepository userRepository;
    private final UserDeviceRepository userDeviceRepository;

    public UserDevice registerToken(String email, String token, String deviceAlias) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserEmailException::new);
        UserDevice build = UserDevice.builder().user(user).deviceToken(token).deviceAlias(deviceAlias).build();
        return userDeviceRepository.save(build);
    }
}
