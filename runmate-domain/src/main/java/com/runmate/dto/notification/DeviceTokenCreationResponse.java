package com.runmate.dto.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@RequiredArgsConstructor
@Getter
public class DeviceTokenCreationResponse {

    private final String deviceAlias;
    @NotBlank private final String token;
}
