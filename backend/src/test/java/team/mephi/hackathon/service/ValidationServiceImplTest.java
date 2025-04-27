package team.mephi.hackathon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceImplTest {

    private ValidationServiceImpl validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationServiceImpl();
    }

    // Позитивные кейсы ИНН
    @Test
    void validateInn_withValid10DigitInn_shouldPass() {
        assertDoesNotThrow(() -> validationService.validateTransaction(
                createDto("1234567890", null)));
    }

    @Test
    void validateInn_withValid12DigitInn_shouldPass() {
        assertDoesNotThrow(() -> validationService.validateTransaction(
                createDto("123456789012", null)));
    }

    // Негативные кейсы ИНН
    @Test
    void validateInn_withInvalidLength_shouldThrowException() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                validationService.validateTransaction(createDto("12345678901", null)));
        assertEquals("ИНН должен содержать 10 или 12 цифр", exception.getMessage());
    }

    @Test
    void validateInn_withNonDigitCharacters_shouldThrowException() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                validationService.validateTransaction(createDto("12345A7890", null)));
        assertEquals("ИНН должен содержать только цифры", exception.getMessage());
    }

    // Позитивные кейсы телефон
    @Test
    void validatePhone_withValidPlus7Format_shouldPass() {
        assertDoesNotThrow(() -> validationService.validateTransaction(
                createDto(null, "+71234567890")));
    }

    @Test
    void validatePhone_withValid8Format_shouldPass() {
        assertDoesNotThrow(() -> validationService.validateTransaction(
                createDto(null, "81234567890")));
    }

    // Негативные кейсы телефон
    @Test
    void validatePhone_withInvalidFormat_shouldThrowException() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                validationService.validateTransaction(createDto(null, "71234567890")));
        assertEquals("Телефон должен быть в формате +7XXXXXXXXXX или 8XXXXXXXXXX", exception.getMessage());
    }

    @Test
    void validatePhone_withInvalidLength_shouldThrowException() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                validationService.validateTransaction(createDto(null, "+7123456789")));
        assertEquals("Телефон должен быть в формате +7XXXXXXXXXX или 8XXXXXXXXXX", exception.getMessage());
    }

    @Test
    void validatePhone_withNonDigitCharacters_shouldThrowException() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                validationService.validateTransaction(createDto(null, "+71234A67890")));
        assertEquals("Телефон должен быть в формате +7XXXXXXXXXX или 8XXXXXXXXXX", exception.getMessage());
    }

    // Вспомогательный метод для создания DTO
    private TransactionRequestDto createDto(String inn, String phone) {
        TransactionRequestDto dto = new TransactionRequestDto();
        dto.setReceiverInn(inn);
        dto.setReceiverPhone(phone);
        return dto;
    }
}
