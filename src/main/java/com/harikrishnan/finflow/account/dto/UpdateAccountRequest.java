package com.harikrishnan.finflow.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class UpdateAccountRequest {
    @NotBlank(message = "Account name should not be blank")
    private String name;
}
