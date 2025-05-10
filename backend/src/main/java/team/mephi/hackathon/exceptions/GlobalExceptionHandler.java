package team.mephi.hackathon.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

/**
 * Глобальный обработчик исключений для REST API. Обрабатывает исключения на уровне приложения и
 * возвращает клиенту понятные ошибки.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
  /** Логгер для записи информации об ошибках и событиях. */
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  public GlobalExceptionHandler() {
    LOGGER.info("GlobalExceptionHandler initialized"); // логирование инициализации
  }

  /**
   * Обрабатывает исключение {@link EntityNotFoundException}. Возвращает HTTP статус 404 (Not Found)
   * и сообщение об ошибке.
   *
   * @param ex выброшенное исключение
   * @return Mono с текстом ошибки
   */
  @ExceptionHandler(EntityNotFoundException.class) // Используем WebFlux исключение
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Mono<String> handleTransactionNotFoundException(EntityNotFoundException ex) {
    return Mono.just(ex.getMessage());
  }

  /**
   * Обрабатывает исключение валидации {@link WebExchangeBindException}. Возвращает HTTP статус 400
   * (Bad Request) и карту полей с ошибками валидации.
   *
   * @param ex исключение, содержащее результаты валидации
   * @return Mono с картой ошибок в формате {"поле": "сообщение"}
   */
  @ExceptionHandler(WebExchangeBindException.class) // Используем WebFlux исключение
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Mono<Map<String, String>> handleWebExchangeBindException(WebExchangeBindException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName =
                  error instanceof FieldError
                      ? ((FieldError) error).getField()
                      : error.getObjectName();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
              LOGGER.debug("Validation error - {}: {}", fieldName, errorMessage);
            });
    return Mono.just(errors);
  }

  /**
   * Обрабатывает пользовательское исключение {@link ValidationException}. Возвращает HTTP статус
   * 400 (Bad Request) и текст сообщения об ошибке.
   *
   * @param ex пользовательское исключение
   * @return Mono с текстом ошибки
   */
  @ExceptionHandler(ValidationException.class) // Используем WebFlux исключение
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Mono<String> handleValidationException(ValidationException ex) {
    return Mono.just(ex.getMessage());
  }
}
