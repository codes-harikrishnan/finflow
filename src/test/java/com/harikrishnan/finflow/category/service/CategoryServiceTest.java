package com.harikrishnan.finflow.category.service;
import com.harikrishnan.finflow.category.domain.Category;
import com.harikrishnan.finflow.category.domain.CategoryType;
import com.harikrishnan.finflow.category.dto.CategoryRequest;
import com.harikrishnan.finflow.category.dto.CategoryResponse;
import com.harikrishnan.finflow.category.repository.CategoryRepository;
import com.harikrishnan.finflow.exceptions.ConflictException;
import com.harikrishnan.finflow.exceptions.ResourceNotFoundException;
import com.harikrishnan.finflow.user.domain.Role;
import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private CategoryService categoryService;

    private User user;

    @BeforeEach
    void setup () {

         user = User.builder()
                .emailId("test@gmail.com")
                .password("abcd1234")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(user,"id",1L);
        when(securityUtils.getCurrentUser()).thenReturn(user);
    }

    @Test
    void addCategory_WithValidRequest_ShouldReturnCategoryResponse () {

        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name("XYZ")
                .type(CategoryType.INCOME)
                .build();

        Category category = Category.builder()
                .name("XYZ")
                .type(CategoryType.INCOME)
                .build();
        ReflectionTestUtils.setField(category,"id",1L);

      when(categoryRepository.existsByNameAndUser(any(String.class),any(User.class))).thenReturn(false);
      when(categoryRepository.save(any(Category.class))).thenReturn(category);
     CategoryResponse categoryResponse = categoryService.addCategory(categoryRequest);
     assertThat(categoryResponse.getId()).isEqualTo(1L);
     assertThat(categoryResponse.getName()).isEqualTo("XYZ");
     assertThat(categoryResponse.getType()).isEqualTo(CategoryType.INCOME);
     assertThat(categoryResponse.getUserId()).isEqualTo(1L);
     verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void addCategory_WithDuplicateName_ShouldThrowConflictException () {
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name("XYZ")
                .type(CategoryType.INCOME)
                .build();

        when(categoryRepository.existsByNameAndUser(any(String.class),any(User.class))).thenReturn(true);
        assertThatThrownBy(() -> categoryService.addCategory(categoryRequest)).isInstanceOf(ConflictException.class);
    }

    @Test
    void getAllCategories_ShouldReturnSystemAndUserCategories () {
        Category category = Category.builder()
                .name("XYZ")
                .type(CategoryType.INCOME)
                .build();
        ReflectionTestUtils.setField(category,"id",2L);
        when(categoryRepository.findByUserIsNullOrUser(any(User.class))).thenReturn(List.of(category));
        List<CategoryResponse> categories = categoryService.getAllCategories();
        assertThat(categories).hasSize(1);
        assertThat(categories.getFirst().getUserId()).isEqualTo(1L);
        assertThat(categories.getFirst().getId()).isEqualTo(2L);
        assertThat(categories.getFirst().getName()).isEqualTo("XYZ");
        assertThat(categories.getFirst().getType()).isEqualTo(CategoryType.INCOME);
  }

    @Test
    void deleteCategory_WithValidId_ShouldDeleteSuccessfully () {
        Category category = Category.builder()
                .name("XYZ")
                .type(CategoryType.INCOME)
                .build();
        ReflectionTestUtils.setField(category,"id",1L);
        when(categoryRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.of(category));
        categoryService.deleteCategory(1L);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_WhenNotFound_ShouldThrowResourceNotFoundException () {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(1L,user)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoryService.deleteCategory(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteCategory_WhenNotOwnedByUser_ShouldThrowResourceNotFoundException () {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(1L,user)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoryService.deleteCategory(1L)).isInstanceOf(ResourceNotFoundException.class);

    }

}
