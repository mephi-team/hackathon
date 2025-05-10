package team.mephi.hackathon.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.support.WebExchangeBindException;

class GlobalExceptionHandlerTest {

  // Заглушечный метод для создания MethodParameter
  public void dummyMethod(Object arg) {}

  @Test
  void handleValidationException_singleFieldError_returnsMapWithOneEntry()
      throws NoSuchMethodException {
    // 1) Подготовка BindingResult с одним FieldError
    MapBindingResult bindingResult = new MapBindingResult(new HashMap<>(), "testObject");
    bindingResult.addError(new FieldError("testObject", "field1", "must not be blank"));

    // 2) Создание WebExchangeBindException
    Method method = GlobalExceptionHandlerTest.class.getMethod("dummyMethod", Object.class);
    MethodParameter parameter = new MethodParameter(method, 0);
    WebExchangeBindException ex = new WebExchangeBindException(parameter, bindingResult);

    // 3) Вызов метода-обработчика и получение результата
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    Map<String, String> errors = handler.handleWebExchangeBindException(ex).block();

    // 4) Проверка результата
    assertNotNull(errors);
    assertEquals(1, errors.size());
    assertEquals("must not be blank", errors.get("field1"));
  }

  @Test
  void handleValidationException_multipleFieldErrors_returnsMapWithAllEntries()
      throws NoSuchMethodException {
    MapBindingResult bindingResult = new MapBindingResult(new HashMap<>(), "obj");
    bindingResult.addError(new FieldError("obj", "f1", "err1"));
    bindingResult.addError(new FieldError("obj", "f2", "err2"));

    Method method = GlobalExceptionHandlerTest.class.getMethod("dummyMethod", Object.class);
    MethodParameter parameter = new MethodParameter(method, 0);
    WebExchangeBindException ex = new WebExchangeBindException(parameter, bindingResult);

    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    Map<String, String> errors = handler.handleWebExchangeBindException(ex).block();

    assertNotNull(errors);
    assertEquals(2, errors.size());
    assertEquals("err1", errors.get("f1"));
    assertEquals("err2", errors.get("f2"));
  }

  @Test
  void handleValidationException_objectError_usesObjectNameAsKey() throws NoSuchMethodException {
    MapBindingResult bindingResult = new MapBindingResult(new HashMap<>(), "myObject");
    bindingResult.addError(new ObjectError("myObject", "generic error"));

    Method method = GlobalExceptionHandlerTest.class.getMethod("dummyMethod", Object.class);
    MethodParameter parameter = new MethodParameter(method, 0);
    WebExchangeBindException ex = new WebExchangeBindException(parameter, bindingResult);

    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    Map<String, String> errors = handler.handleWebExchangeBindException(ex).block();

    assertNotNull(errors);
    assertEquals(1, errors.size());
    assertEquals("generic error", errors.get("myObject"));
  }
}

class ExceptionsTest {
  @Test
  void transactionNotFoundException_message() {
    String msg = "Transaction 123 not found";
    EntityNotFoundException ex = new EntityNotFoundException(msg);
    assertEquals(msg, ex.getMessage());
  }
}
