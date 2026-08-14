package com.harikrishnan.finflow.user.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Builder
public class AuthResponseDto {
    private final String token;
}
