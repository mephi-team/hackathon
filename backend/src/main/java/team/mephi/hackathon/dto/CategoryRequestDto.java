package team.mephi.hackathon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO (Data Transfer Object) для создания и обновления категории транзакции. Используется как
 * входной параметр при работе с REST API для управления категориями.
 */
@Data
public class CategoryRequestDto {
  /** Наименование категории транзакции. Обязательное поле. */
  @NotBlank(message = "Наименование категории обязательно")
  private String name;
}
