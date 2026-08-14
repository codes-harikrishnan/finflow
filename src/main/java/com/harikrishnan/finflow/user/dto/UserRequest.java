package com.harikrishnan.finflow.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder

public class UserRequest {

    @NotBlank(message = "Email id should not be blank")
    @Email(message = "Email id should be in proper format")
    private String emailId;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 6, message = "Password should be with minimum 6 characters")
    private String password;

}
