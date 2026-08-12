package com.harikrishnan.finflow.user.service;

import com.harikrishnan.finflow.exceptions.UserAlreadyExistsException;
import com.harikrishnan.finflow.user.domain.Role;
import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.user.dto.AuthResponseDto;
import com.harikrishnan.finflow.user.dto.UserRequest;
import com.harikrishnan.finflow.user.repository.UserRepository;
import com.harikrishnan.finflow.user.util.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public void registerUser (UserRequest userRequest) {
        if(userRepository.existsUserByEmailId(userRequest.getEmailId())) {
            throw new UserAlreadyExistsException("A user already exists with the email: " + userRequest.getEmailId());
        }

        User newUser = User.builder()
                .emailId(userRequest.getEmailId())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(newUser);
    }

    public AuthResponseDto authenticateUser (UserRequest userRequest) {
        log.info("authenticateUser {}, {}", userRequest.getEmailId(),userRequest.getPassword());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getEmailId(),userRequest.getPassword()));
        log.info("authenticated");
        return AuthResponseDto.builder()
                .token(jwtService.generateAccessToken(userRequest.getEmailId()))
                .build();
    }
}
