package com.harikrishnan.finflow.user.service;

import com.harikrishnan.finflow.exceptions.UserAlreadyExistsException;
import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.user.dto.AuthResponseDto;
import com.harikrishnan.finflow.user.dto.UserRequest;
import com.harikrishnan.finflow.user.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.harikrishnan.finflow.user.util.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;


    @Test
     void registerUser_WithValidUserRequest_ShouldSaveUser() {

        when(userRepository.existsUserByEmailId(any(String.class))).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("xyz321");

       UserRequest userRequest = UserRequest.builder()
               .emailId("test@gmail.com")
               .password("abc123")
               .build();

       userService.registerUser(userRequest);

       verify(userRepository).save(any(User.class));

    }

    @Test
     void registerUser_WithAlreadyExistingEmail_ShouldReturnUserAlreadyExistException () {
        when(userRepository.existsUserByEmailId(any(String.class))).thenReturn(true);

        UserRequest userRequest = UserRequest.builder()
                .emailId("test@gmail.com")
                .password("abc123")
                .build();

        assertThatThrownBy(() -> userService.registerUser(userRequest)).isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void authenticateUser_WithRegisteredUserCredentials_ShouldReturnToken () {
        when(jwtService.generateAccessToken(any(String.class))).thenReturn("ABCD");

        UserRequest userRequest = UserRequest.builder()
                .emailId("test@gmail.com")
                .password("abc123")
                .build();

        AuthResponseDto authResponseDto = userService.authenticateUser(userRequest);
        assertThat(authResponseDto.getToken()).isEqualTo("ABCD");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateUser_WithInvalidUserCredentials_ShouldReturnToken () {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        UserRequest userRequest = UserRequest.builder()
                .emailId("test@gmail.com")
                .password("abc1234")
                .build();

       assertThatThrownBy(() -> userService.authenticateUser(userRequest)).isInstanceOf(BadCredentialsException.class);
        
    }



}
