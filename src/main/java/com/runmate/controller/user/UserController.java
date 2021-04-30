package com.runmate.controller.user;

import com.runmate.domain.user.User;
import com.runmate.service.user.UserService;
import com.runmate.utils.JsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/{passedEmail}")
    public ResponseEntity<JsonWrapper> get(@PathVariable("passedEmail") String passedEmail) {
        User user=userService.getUser(passedEmail);
        JsonWrapper jsonWrapper=JsonWrapper.builder()
                .data(user)
                .error(null)
                .build();

        return ResponseEntity.ok()
                .body(jsonWrapper);
    }

    @PutMapping("/{passedEmail}")
    public ResponseEntity<String> modify(@RequestParam("email") String tokenEmail,
                                         @PathVariable("passedEmail") String passedEmail,
                                         @Valid @RequestBody User user) {

        if (!tokenEmail.equals(passedEmail))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("it's not your email");

        userService.modify(passedEmail, user);
        return ResponseEntity.ok().body("success");
    }
}
