package team.mephi.hackathon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import team.mephi.hackathon.controller.CategoryService;
import team.mephi.hackathon.dto.CategoryRequestDto;
import team.mephi.hackathon.dto.CategoryResponseDto;
import team.mephi.hackathon.entity.Category;
import team.mephi.hackathon.repository.CategoryRepository;

class CategoryServiceTest {

  private CategoryService categoryService;
  private CategoryRepository categoryRepository;
  private final UUID categoryId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    categoryRepository = mock(CategoryRepository.class);
    ModelMapper modelMapper = new ModelMapper();
    categoryService = new CategoryServiceImpl(categoryRepository, modelMapper);
  }

  @Test
  void shouldCreateNewCategory() {
    CategoryRequestDto dto = new CategoryRequestDto("SALARY");
    Category entity = new Category();
    entity.setId(categoryId);
    entity.setName("SALARY");

    when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(entity);

    CategoryResponseDto result = categoryService.createCategory(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("SALARY");
    verify(categoryRepository, times(1)).save(any(Category.class));
  }

  @Test
  void shouldThrowExceptionWhenCreatingWithNullName() {
    CategoryRequestDto dto = new CategoryRequestDto("");

    assertThatThrownBy(() -> categoryService.createCategory(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("source cannot be null");
  }

  @Test
  void shouldUpdateCategoryName() {
    Category existing = new Category();
    existing.setId(categoryId);
    existing.setName("OLD_NAME");

    CategoryRequestDto dto = new CategoryRequestDto("NEW_NAME");

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
    when(categoryRepository.save(any(Category.class))).thenReturn(existing);

    CategoryResponseDto result = categoryService.updateCategory(categoryId, dto);

    assertThat(result.getName()).isEqualTo("NEW_NAME");
    verify(categoryRepository, times(1)).save(existing);
  }

  @Test
  void shouldDeleteCategory() {
    Category existing = new Category();
    existing.setId(categoryId);
    existing.setName("OLD_NAME");

    doNothing().when(categoryRepository).deleteById(categoryId);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));

    categoryService.deleteCategory(categoryId);

    verify(categoryRepository, times(1)).deleteById(categoryId);
  }
}
