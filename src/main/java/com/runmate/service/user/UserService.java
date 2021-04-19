package com.runmate.service.user;

import com.runmate.domain.dto.AuthRequest;
import com.runmate.domain.user.User;

public interface UserService {
    boolean join(User user);
    boolean login(AuthRequest request);
    User getUser(String email);
}
