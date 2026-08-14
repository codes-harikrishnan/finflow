package com.harikrishnan.finflow.user.util.jwt;

import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilterInternal");
        String authorization = request.getHeader("Authorization");
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request,response);
            return;
        }

        try {
            String token = authorization.substring(7);

            String emailId = jwtService.extractEmailIdFromToken(token);

            User user = userRepository.findByEmailId(emailId);

            if(SecurityContextHolder.getContext().getAuthentication() == null && jwtService.isTokenValid(token,user.getEmailId())) {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmailId(),null, List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole()))));
            }
        }
        catch (Exception ex) {
            log.error("Failed to process JWT token : {}", ex.getMessage());
        }

        filterChain.doFilter(request,response);
    }
}
