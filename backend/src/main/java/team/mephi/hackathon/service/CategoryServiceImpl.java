package team.mephi.hackathon.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.controller.CategoryService;
import team.mephi.hackathon.dto.CategoryRequestDto;
import team.mephi.hackathon.dto.CategoryResponseDto;
import team.mephi.hackathon.entity.Category;
import team.mephi.hackathon.exceptions.EntityNotFoundException;
import team.mephi.hackathon.repository.CategoryRepository;

/**
 * Реализация сервисного слоя для работы с категориями транзакций. Обеспечивает бизнес-логику
 * создания, чтения, обновления и удаления категорий.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  /** Репозиторий для работы с сущностью {@link Category}. Инжектируется через конструктор. */
  private final CategoryRepository repository;

  /** Маппер для преобразования между DTO и Entity. Инжектируется через конструктор. */
  private final ModelMapper modelMapper;

  /**
   * Получает список всех доступных категорий.
   *
   * @return список {@link CategoryResponseDto} — все существующие категории
   */
  public List<CategoryResponseDto> getCategories() {
    return repository.findAll().stream().map(this::mapToDto).toList();
  }

  /**
   * Создаёт новую категорию на основе переданных данных.
   *
   * @param dto данные о новой категории
   * @return созданная категория в виде {@link CategoryResponseDto}
   */
  public CategoryResponseDto createCategory(CategoryRequestDto dto) {
    Category entity = mapToEntity(dto);

    return mapToDto(repository.save(entity));
  }

  /**
   * Обновляет существующую категорию по её ID.
   *
   * @param id идентификатор категории, которую нужно обновить
   * @param dto новые данные для категории
   * @return обновлённая категория в виде {@link CategoryResponseDto}
   */
  public CategoryResponseDto updateCategory(UUID id, CategoryRequestDto dto) {
    Category entity = getExistingCategoryOrThrowException(id);
    entity.setName(dto.getName());

    return mapToDto(repository.save(entity));
  }

  /**
   * Удаляет категорию по её идентификатору.
   *
   * @param id идентификатор категории, которую нужно удалить
   */
  public void deleteCategory(UUID id) {
    getExistingCategoryOrThrowException(id);

    repository.deleteById(id);
  }

  /**
   * Преобразует сущность {@link Category} в DTO.
   *
   * @param category исходная категория
   * @return объект {@link CategoryResponseDto}
   */
  private CategoryResponseDto mapToDto(Category category) {
    return modelMapper.map(category, CategoryResponseDto.class);
  }

  /**
   * Преобразует DTO в сущность {@link Category}.
   *
   * @param dto исходные данные
   * @return объект {@link Category}
   */
  private Category mapToEntity(CategoryRequestDto dto) {
    return modelMapper.map(dto, Category.class);
  }

  /**
   * Возвращает категорию по её идентификатору или выбрасывает исключение, если категория не
   * найдена.
   *
   * @param id идентификатор категории
   * @return найденная категория
   * @throws EntityNotFoundException если категория не найдена
   */
  private Category getExistingCategoryOrThrowException(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category " + id + " not found."));
  }
}
