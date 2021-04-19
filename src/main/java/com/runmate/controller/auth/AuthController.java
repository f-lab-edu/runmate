package com.runmate.controller.auth;


import com.runmate.configure.jwt.JwtUtils;
import com.runmate.configure.oauth.kakao.KakaoApi;
import com.runmate.domain.dto.AuthRequest;
import com.runmate.domain.user.CrewRole;
import com.runmate.domain.user.User;
import com.runmate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RequestMapping("/api/auth/*")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final KakaoApi kakaoApi;

    @PostMapping("/local/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request){
        if(userService.login(request))
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION,"Bearer "+JwtUtils.createToken(request.getEmail()))
                    .body("success");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @PostMapping("/local/new")
    public ResponseEntity<String>join(@RequestBody User user){
        user.setCrewRole(CrewRole.NO);
        if(userService.join(user)){
            return ResponseEntity.ok()
                    .body("success");
        }else {
            return ResponseEntity.ok()
                    .body("failed");
        }
    }

    @RequestMapping("/kakao/login")
    public ResponseEntity kakaoLogin(@RequestParam(name = "code",required = false)String code){
        if(code==null){
            try {
                HttpHeaders httpHeaders=new HttpHeaders();
                httpHeaders.setLocation(new URI(kakaoApi.getRedirectionUri()));
                return new ResponseEntity(httpHeaders,HttpStatus.SEE_OTHER);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }else{
            String email=kakaoApi.getEmail(code);

            if(userService.getUser(email)==null) {
                User user=new User();
                user.setEmail(email);
                userService.join(user);
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION,"Bearer "+JwtUtils.createToken(email))
                    .build();
        }
    }
}
