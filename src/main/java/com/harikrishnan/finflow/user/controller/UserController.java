package com.harikrishnan.finflow.user.controller;

import com.harikrishnan.finflow.user.dto.AuthResponseDto;
import com.harikrishnan.finflow.user.dto.UserRequest;
import com.harikrishnan.finflow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser (@RequestBody @Valid UserRequest userRequest) {
        log.info("/register");
         userService.registerUser(userRequest);
         return ResponseEntity.status(HttpStatus.CREATED).body("User has been registered with email id:" + userRequest.getEmailId());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticateuser (@RequestBody @Valid UserRequest userRequest) {
        log.info("/authenticate");
        return ResponseEntity.status(HttpStatus.OK).body(userService.authenticateUser(userRequest));
    }
}
