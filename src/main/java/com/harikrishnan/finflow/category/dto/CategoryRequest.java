package com.harikrishnan.finflow.category.dto;

import com.harikrishnan.finflow.category.domain.CategoryType;
import com.harikrishnan.finflow.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CategoryRequest {

    @NotBlank(message = "Category name cannot be blank")
    private String name;

    @NotNull(message = "Category type cannot be null")
    private CategoryType type;

}
