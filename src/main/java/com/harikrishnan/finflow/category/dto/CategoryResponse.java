package com.harikrishnan.finflow.category.dto;

import com.harikrishnan.finflow.category.domain.CategoryType;
import com.harikrishnan.finflow.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@AllArgsConstructor
@Getter
@Builder
public class CategoryResponse {

    private final Long id;

    private final String name;

    private final Long userId;

    private final CategoryType type;
}
