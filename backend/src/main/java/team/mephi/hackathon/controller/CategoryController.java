package team.mephi.hackathon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team.mephi.hackathon.dto.CategoryRequestDto;
import team.mephi.hackathon.dto.CategoryResponseDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @GetMapping
    public List<CategoryResponseDto> getCategories() {
        return service.getCategories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(@Valid @RequestBody CategoryRequestDto dto) {
        return service.createCategory(dto);
    }

    @PutMapping("/{id}")
    public CategoryResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequestDto dto
    ) {
        return service.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable UUID id) {
        service.deleteCategory(id);
    }
}
