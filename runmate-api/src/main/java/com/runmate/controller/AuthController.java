package com.runmate.controller;

import com.runmate.configure.jwt.JwtProvider;
import com.runmate.configure.oauth.kakao.KakaoApi;
import com.runmate.domain.user.User;
import com.runmate.dto.AuthRequest;
import com.runmate.dto.user.UserCreationDto;
import com.runmate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
        User loginUser = userService.login(request);
        URI uri = WebMvcLinkBuilder.linkTo(UserController.class).slash(loginUser.getId()).toUri();
        return ResponseEntity.created(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createToken(request.getEmail()))
                .body("success");
    }

    @PostMapping("/local/new")
    public ResponseEntity<String> join(@RequestBody @Valid UserCreationDto creationDto) {
        User joined = userService.join(modelMapper.map(creationDto, User.class));
        URI uri = WebMvcLinkBuilder.linkTo(UserController.class).slash(joined.getId()).toUri();
        return ResponseEntity.created(uri).body("success");
    }

    @RequestMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin(@RequestParam(name = "code", required = false) String code) throws URISyntaxException {
        if (code == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(new URI(kakaoApi.getRedirectionUri()));

            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        }

        String email = kakaoApi.getEmail(code);
        if (userService.findByEmail(email) == null) {
            User user = User.of()
                    .email(email)
                    .build();
            userService.join(user);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createToken(email))
                .build();
    }
}
