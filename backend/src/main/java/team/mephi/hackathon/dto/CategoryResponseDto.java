package team.mephi.hackathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для передачи данных о категории транзакции клиенту. Используется как
 * ответ на запросы, связанные с операциями над категориями.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель категории транзакции")
public class CategoryResponseDto {
  /** Уникальный идентификатор категории. Генерируется автоматически при создании категории. */
  @Schema(
      description = "Уникальный идентификатор категории",
      example = "550e8400-e29b-41d4-a716-446655440000")
  private UUID id;

  /** Наименование категории транзакции. */
  @Schema(description = "Наименование категории транзакции")
  private String name;
}
