package team.mephi.hackathon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Сущность, представляющая категорию транзакции. Используется для классификации транзакций по типам
 * операций.
 */
@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "categories")
public class Category {
  /** Уникальный идентификатор категории. Генерируется автоматически при создании. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Наименование категории. Должно быть обязательным. */
  @Column(nullable = false)
  @NonNull
  private String name;
}
