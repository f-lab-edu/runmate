package com.runmate.dto.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class DeviceTokenCreationRequest {

    @Email private final String email;
    @NotBlank private final String token;
    private final String alias;
}
