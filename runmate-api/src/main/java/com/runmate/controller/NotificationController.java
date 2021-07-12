package com.runmate.controller;

import com.runmate.domain.user.UserDevice;
import com.runmate.dto.notification.DeviceTokenCreationRequest;
import com.runmate.dto.notification.DeviceTokenCreationResponse;
import com.runmate.service.notification.NotificationService;
import com.runmate.utils.JsonWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/tokens")
    public ResponseEntity<JsonWrapper> registerToken(@RequestBody DeviceTokenCreationRequest request) {
        UserDevice userDevice = notificationService.registerToken(request.getEmail(), request.getToken(), request.getAlias());
        DeviceTokenCreationResponse data = DeviceTokenCreationResponse.builder()
                .token(userDevice.getDeviceToken())
                .deviceAlias(userDevice.getDeviceAlias())
                .build();

        JsonWrapper body = JsonWrapper.success(data);
        URI uri = WebMvcLinkBuilder.linkTo(NotificationController.class).slash("tokens").slash(userDevice.getId()).toUri();
        return ResponseEntity.created(uri).body(body);
    }
}
