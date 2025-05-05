package team.mephi.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.controller.CategoryService;
import team.mephi.hackathon.dto.CategoryRequestDto;
import team.mephi.hackathon.dto.CategoryResponseDto;
import team.mephi.hackathon.entity.*;
import team.mephi.hackathon.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    public List<CategoryResponseDto> getCategories() {
        return repository.findAll().stream().map(this::mapToDto).toList();
    }

    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        Category entity = mapToEntity(dto);
        return mapToDto(repository.save(entity));
    }

    private CategoryResponseDto mapToDto(Category category) {
        return new CategoryResponseDto(category.getId(), category.getName());
    }

    private Category mapToEntity(CategoryRequestDto dto) {
        return new Category(dto.getName());
    }
}
