package com.harikrishnan.finflow.user.service;

import com.harikrishnan.finflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername");
        if(!userRepository.existsUserByEmailId(username)) {
            log.info("Unable to find a user with an email id:"+username);
            throw new UsernameNotFoundException("Unable to find a user with an email id: " + username);
        }

        com.harikrishnan.finflow.user.domain.User dbUser = userRepository.findByEmailId(username);
        log.info("dbUser:",dbUser.getEmailId());
        return User.builder()
                .username(dbUser.getEmailId())
                .password(dbUser.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_"+dbUser.getRole())))
                .build();
    }
}
