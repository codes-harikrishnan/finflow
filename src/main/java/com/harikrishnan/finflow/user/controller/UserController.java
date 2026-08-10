package com.harikrishnan.finflow.user.controller;

import com.harikrishnan.finflow.user.dto.UserRequest;
import com.harikrishnan.finflow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser (@Valid @RequestBody UserRequest userRequest) {
         userService.registerUser(userRequest);
         return ResponseEntity.status(HttpStatus.CREATED).body("User has been registered with email id:" + userRequest.getEmailId());
    }
}
