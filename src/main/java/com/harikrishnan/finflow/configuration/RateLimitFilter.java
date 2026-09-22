package com.harikrishnan.finflow.configuration;

import com.harikrishnan.finflow.utils.RateLimiteService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiteService rateLimiteService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String identifier = rateLimiteService.extractIdentifier(request);

        if(! rateLimiteService.isAllowed(identifier)) {
            response.setStatus(429);
            response.setHeader("X-RateLimit-Limit","100");
            response.setHeader("X-RateLimit-Remaining","0");
            response.setHeader("Retry-After","60");
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":429,\"message\":\"Too many requests. Please try again later.\"}");
        }
        response.setHeader("X-RateLimit-Limit","100");
        response.setHeader("X-RateLimit-Remaining",String.valueOf(rateLimiteService.getRemainingRquests(identifier)));

        filterChain.doFilter(request,response);
    }
}
