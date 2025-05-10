package team.mephi.hackathon.dto;

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
public class CategoryResponseDto {
  /** Уникальный идентификатор категории. Генерируется автоматически при создании категории. */
  private UUID id;

  /** Наименование категории транзакции. */
  private String name;
}
