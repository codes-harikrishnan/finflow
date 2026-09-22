package com.harikrishnan.finflow.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiteService {

    private final RedisTemplate<String,String> redisTemplate;

    private static final int MAX_REQUESTS = 100;

    private static final int WINDOW_SECONDS = 60;

    public boolean isAllowed (String identifier) {

        String key = "rate_limit_"+identifier;

        Long count = redisTemplate.opsForValue().increment(key);

        if(count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS));
        }
        return count <= MAX_REQUESTS;
    }

    public long getRemainingRquests (String identifier) {
        String key = "rate_limit_"+identifier;
        String requestsConsumed = redisTemplate.opsForValue().get(key);
        long used = requestsConsumed != null ? Long.parseLong(requestsConsumed) : 0;
        return Math.max(0,MAX_REQUESTS - used);
    }

    public String extractIdentifier (HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7,Math.min(authHeader.length(),50));
        }
        String ip = request.getHeader("X-Forwarded-For");
        return ip != null ? ip : request.getRemoteAddr();
    }
}
