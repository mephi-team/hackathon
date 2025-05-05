package team.mephi.hackathon.controller;

import team.mephi.hackathon.dto.CategoryRequestDto;
import team.mephi.hackathon.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> getCategories();
    CategoryResponseDto createCategory(CategoryRequestDto category);
}
