package com.harikrishnan.finflow.user.dto;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class UserRequest {

    private String emailId;

    private String password;

}
