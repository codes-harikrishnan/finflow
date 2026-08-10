package com.harikrishnan.finflow.user.service;

import com.harikrishnan.finflow.exceptions.UserAlreadyExistsException;
import com.harikrishnan.finflow.user.domain.Role;
import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.user.dto.UserRequest;
import com.harikrishnan.finflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

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



}
