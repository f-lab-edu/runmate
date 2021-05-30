package com.runmate.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthRequest {
    private final String email;
    private final String password;
}
