package com.harikrishnan.finflow.utils;

import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public User getCurrentUser () {
        if( SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new UsernameNotFoundException("Unable to find an authorized user for this request");
        }

        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailId(emailId);
        if(user == null) {
            throw new UsernameNotFoundException("User not found: " + emailId);
        }
        return user;
    }
}
