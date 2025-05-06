package team.mephi.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.controller.CategoryService;
import team.mephi.hackathon.dto.CategoryRequestDto;
import team.mephi.hackathon.dto.CategoryResponseDto;
import team.mephi.hackathon.entity.*;
import team.mephi.hackathon.exceptions.EntityNotFoundException;
import team.mephi.hackathon.repository.CategoryRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final ModelMapper modelMapper;

    public List<CategoryResponseDto> getCategories() {
        return repository.findAll().stream().map(this::mapToDto).toList();
    }

    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        Category entity = mapToEntity(dto);

        return mapToDto(repository.save(entity));
    }

    public CategoryResponseDto updateCategory(UUID id, CategoryRequestDto dto) {
        Category entity = getExistingCategoryOrThrowException(id);
        entity.setName(dto.getName());

        return mapToDto(repository.save(entity));
    }

    public void deleteCategory(UUID id) {
        getExistingCategoryOrThrowException(id);

        repository.deleteById(id);
    }

    private CategoryResponseDto mapToDto(Category category) {
        return modelMapper.map(category, CategoryResponseDto.class);
    }

    private Category mapToEntity(CategoryRequestDto dto) {
        return modelMapper.map(dto, Category.class);
    }

    private Category getExistingCategoryOrThrowException(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category " + id + " not found."));
    }
}
