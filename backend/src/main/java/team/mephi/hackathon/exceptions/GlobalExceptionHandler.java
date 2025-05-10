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

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  public GlobalExceptionHandler() {
    LOGGER.info("GlobalExceptionHandler initialized"); // логирование инициализации
  }

  @ExceptionHandler(EntityNotFoundException.class) // Используем WebFlux исключение
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Mono<String> handleTransactionNotFoundException(EntityNotFoundException ex) {
    return Mono.just(ex.getMessage());
  }

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

  @ExceptionHandler(ValidationException.class) // Используем WebFlux исключение
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Mono<String> handleValidationException(ValidationException ex) {
    return Mono.just(ex.getMessage());
  }
}
