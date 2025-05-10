package team.mephi.hackathon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.mephi.hackathon.dto.CategoryRequestDto;
import team.mephi.hackathon.dto.CategoryResponseDto;

/**
 * REST-контроллер для управления категориями транзакций. Предоставляет CRUD-операции через HTTP
 * API.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(
    name = "Категории",
    description = "API для создания, чтения, обновления и удаления категорий транзакций")
public class CategoryController {
  /** Сервис для работы с категориями. Инжектируется через конструктор. */
  private final CategoryService service;

  /**
   * Получает список всех доступных категорий.
   *
   * @return список {@link CategoryResponseDto} с информацией о категориях
   */
  @GetMapping
  @Operation(
      summary = "Получить все категории",
      description = "Возвращает список всех активных категорий транзакций",
      responses = {
        @ApiResponse(responseCode = "200", description = "Список категорий успешно получен")
      })
  public List<CategoryResponseDto> getCategories() {
    return service.getCategories();
  }

  /**
   * Создаёт новую категорию.
   *
   * @param dto данные категории, переданные клиентом
   * @return созданная категория в виде {@link CategoryResponseDto}
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Создать категорию",
      description = "Принимает данные о новой категории и сохраняет её в системе",
      responses = {
        @ApiResponse(responseCode = "201", description = "Категория успешно создана"),
        @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации входных данных",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Категория с таким именем уже существует",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  public CategoryResponseDto createCategory(@Valid @RequestBody CategoryRequestDto dto) {
    return service.createCategory(dto);
  }

  /**
   * Обновляет существующую категорию по её ID.
   *
   * @param id идентификатор категории, которую нужно обновить
   * @param dto новые данные для категории
   * @return обновлённая категория в виде {@link CategoryResponseDto}
   */
  @PutMapping("/{id}")
  @Operation(
      summary = "Обновить категорию",
      description = "Обновляет данные существующей категории",
      responses = {
        @ApiResponse(responseCode = "200", description = "Категория успешно обновлена"),
        @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Категория не найдена",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  public CategoryResponseDto update(
      @PathVariable UUID id, @Valid @RequestBody CategoryRequestDto dto) {
    return service.updateCategory(id, dto);
  }

  /**
   * Удаляет категорию по её ID.
   *
   * @param id идентификатор категории, которую нужно удалить
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Удалить категорию",
      description = "Логически или физически удаляет категорию из системы",
      responses = {
        @ApiResponse(responseCode = "204", description = "Категория успешно удалена"),
        @ApiResponse(
            responseCode = "404",
            description = "Категория не найдена",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  public void deleteCategory(@PathVariable UUID id) {
    service.deleteCategory(id);
  }
}
