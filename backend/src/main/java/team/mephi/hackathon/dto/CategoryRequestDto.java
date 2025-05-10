package team.mephi.hackathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для создания и обновления категории транзакции. Используется как
 * входной параметр при работе с REST API для управления категориями.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания или обновления категории транзакции")
public class CategoryRequestDto {
  /** Наименование категории транзакции. Обязательное поле. */
  @NotBlank(message = "Наименование категории обязательно")
  @Schema(description = "Наименование категории транзакции")
  private String name;
}
