package com.harikrishnan.finflow.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Data
@Builder
public class ExceptionResponseDto {

    private final String message;

    private final Integer statusCode;

    private Map<String,String> errors;

}
