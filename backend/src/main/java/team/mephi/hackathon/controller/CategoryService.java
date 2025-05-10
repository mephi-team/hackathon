package team.mephi.hackathon.controller;

import java.util.List;
import java.util.UUID;
import team.mephi.hackathon.dto.CategoryRequestDto;
import team.mephi.hackathon.dto.CategoryResponseDto;

public interface CategoryService {
  List<CategoryResponseDto> getCategories();

  CategoryResponseDto createCategory(CategoryRequestDto category);

  CategoryResponseDto updateCategory(UUID id, CategoryRequestDto dto);

  void deleteCategory(UUID id);
}
