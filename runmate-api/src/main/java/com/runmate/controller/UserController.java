package com.runmate.controller;

import com.runmate.domain.user.User;
import com.runmate.dto.user.UserGetDto;
import com.runmate.dto.user.UserModificationDto;
import com.runmate.service.user.UserImageUploadService;
import com.runmate.service.user.UserService;
import com.runmate.utils.JsonWrapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final UserImageUploadService imageUploadService;

    @GetMapping("/{passedEmail}")
    public ResponseEntity<JsonWrapper> get(@PathVariable("passedEmail") String passedEmail) {
        User user = userService.findByEmail(passedEmail);

        JsonWrapper jsonWrapper = JsonWrapper.success(modelMapper.map(user, UserGetDto.class));

        return ResponseEntity.ok()
                .body(jsonWrapper);
    }

    @PutMapping("/{passedEmail}")
    public ResponseEntity<JsonWrapper> modify(@RequestParam("email") String tokenEmail,
                                              @PathVariable("passedEmail") String passedEmail,
                                              @Valid @RequestBody UserModificationDto modificationDto) {
        if (!tokenEmail.equals(passedEmail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JsonWrapper.error("it's not your email"));
        }

        User modified = userService.modify(passedEmail, modificationDto);
        UserGetDto body = modelMapper.map(modified, UserGetDto.class);
        return ResponseEntity.ok()
                .body(JsonWrapper.success(body));
    }

    @PostMapping("/{passedEmail}/thumbnail")
    public ResponseEntity<JsonWrapper> uploadImage(@RequestParam("file") MultipartFile targetFile,
                                                   @RequestParam("email") String tokenEmail,
                                                   @PathVariable("passedEmail") String passedEmail) {
        if (!tokenEmail.equals(passedEmail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JsonWrapper.error("it's not your email"));
        }
        String keyName = imageUploadService.saveImage(targetFile, tokenEmail);
        return ResponseEntity.ok()
                .body(JsonWrapper.success(keyName));
    }
}
