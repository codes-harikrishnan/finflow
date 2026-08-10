package com.harikrishnan.finflow.user.service;

import com.harikrishnan.finflow.exceptions.UserAlreadyExistsException;
import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.user.dto.UserRequest;
import com.harikrishnan.finflow.user.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void registerUser_WithValidUserRequest_ShouldSaveUser() {

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
    public void registerUser_WithAlreadyExistingEmail_ShouldReturnUserAlreadyExistException () {
        when(userRepository.existsUserByEmailId(any(String.class))).thenReturn(true);

        UserRequest userRequest = UserRequest.builder()
                .emailId("test@gmail.com")
                .password("abc123")
                .build();

        assertThatThrownBy(() -> userService.registerUser(userRequest)).isInstanceOf(UserAlreadyExistsException.class);
    }

}
