package com.harikrishnan.finflow.category.service;

import com.harikrishnan.finflow.category.domain.Category;
import com.harikrishnan.finflow.category.dto.CategoryRequest;
import com.harikrishnan.finflow.category.dto.CategoryResponse;
import com.harikrishnan.finflow.category.repository.CategoryRepository;
import com.harikrishnan.finflow.exceptions.ConflictException;
import com.harikrishnan.finflow.exceptions.ResourceNotFoundException;
import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final SecurityUtils securityUtils;

    @Transactional
    public CategoryResponse addCategory (CategoryRequest categoryRequest) {
        User user = securityUtils.getCurrentUser();
        if(categoryRepository.existsByNameAndUser(categoryRequest.getName(), user)) {
            throw new ConflictException("A category with the name '" + categoryRequest.getName() + "' already exists");
        }

        Category newCategory = Category.builder()
                .name(categoryRequest.getName())
                .type(categoryRequest.getType())
                .user(user)
                .build();

     Category category = categoryRepository.save(newCategory);

     return CategoryResponse.builder()
             .id(category.getId())
             .name(category.getName())
             .type(category.getType())
             .userId(user.getId())
             .build();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories () {
        User user = securityUtils.getCurrentUser();
        List<Category> categories = categoryRepository.findByUserIsNullOrUser(user);
        return categories.stream().map(category -> CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .userId(user.getId())
                .build()).toList();
    }

    @Transactional
    public void deleteCategory(Long id) {
        User user = securityUtils.getCurrentUser();
        Category category = categoryRepository.findByIdAndUser(id,user).orElseThrow(()-> new ResourceNotFoundException("Unable to find the category with id " + id + " for the user with id "+ user.getId()));
        categoryRepository.delete(category);

    }

}
