package com.runmate.controller.auth;


import com.runmate.configure.jwt.JwtProvider;
import com.runmate.configure.oauth.kakao.KakaoApi;
import com.runmate.domain.dto.AuthRequest;
import com.runmate.domain.user.User;
import com.runmate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final KakaoApi kakaoApi;
    private final JwtProvider jwtProvider;

    @PostMapping("/local/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request){
        if(userService.login(request))
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION,"Bearer "+ jwtProvider.createToken(request.getEmail()))
                    .body("success");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @PostMapping("/local/new")
    public ResponseEntity<String>join(@RequestBody User user){
        if(userService.join(user)){
            return ResponseEntity.ok()
                    .body("success");
        }else {
            return ResponseEntity.ok()
                    .body("failed");
        }
    }

    @RequestMapping("/kakao/login")
    public ResponseEntity kakaoLogin(@RequestParam(name = "code",required = false)String code) throws URISyntaxException {
        if(code==null){
            HttpHeaders httpHeaders=new HttpHeaders();
            httpHeaders.setLocation(new URI(kakaoApi.getRedirectionUri()));

            return new ResponseEntity(httpHeaders,HttpStatus.SEE_OTHER);
        }else{
            String email=kakaoApi.getEmail(code);
            if(userService.getUser(email)==null) {
                User user=new User();
                user.setEmail(email);
                userService.join(user);
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION,"Bearer "+ jwtProvider.createToken(email))
                    .build();
        }
    }
}
