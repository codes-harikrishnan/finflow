package com.harikrishnan.finflow.user.util.jwt;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
@Data
@RequiredArgsConstructor
public class JwtConfiguration {

        private    String secret;

        private  Long accessTokenExpiration;
}
