package com.runmate.controller;


import com.runmate.configure.jwt.JwtProvider;
import com.runmate.configure.oauth.kakao.KakaoApi;
import com.runmate.dto.AuthRequest;
import com.runmate.dto.user.UserCreationDto;
import com.runmate.domain.user.User;
import com.runmate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final KakaoApi kakaoApi;
    private final JwtProvider jwtProvider;
    private final ModelMapper modelMapper;

    @PostMapping("/local/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        if (userService.login(request))
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createToken(request.getEmail()))
                    .body("success");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/local/new")
    public ResponseEntity<String> join(@RequestBody @Valid UserCreationDto creationDto) {
        if (userService.join(modelMapper.map(creationDto, User.class))) {
            return ResponseEntity.ok()
                    .body("success");
        } else {
            return ResponseEntity.ok()
                    .body("failed");
        }
    }

    @RequestMapping("/kakao/login")
    public ResponseEntity kakaoLogin(@RequestParam(name = "code", required = false) String code) throws URISyntaxException {
        if (code == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(new URI(kakaoApi.getRedirectionUri()));

            return new ResponseEntity(httpHeaders, HttpStatus.SEE_OTHER);
        } else {
            String email = kakaoApi.getEmail(code);
            if (userService.getUser(email) == null) {
                User user = new User();
                user.setEmail(email);
                userService.join(user);
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createToken(email))
                    .build();
        }
    }
}
