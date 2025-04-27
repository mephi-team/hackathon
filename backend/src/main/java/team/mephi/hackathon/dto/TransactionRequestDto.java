package team.mephi.hackathon.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRequestDto {
    @NotBlank(message = "Тип лица обязателен")
    private String personType; // PHYSICAL / LEGAL

    @NotNull(message = "Дата операции обязательна")
    private LocalDateTime operationDate;

    @NotBlank(message = "Тип транзакции обязателен")
    private String transactionType; // INCOME / OUTCOME

    @Size(max = 500, message = "Комментарий слишком длинный")
    private String comment;

    @NotNull(message = "Сумма обязательна")
    @Digits(integer = 15, fraction = 5, message = "Некорректный формат суммы")
    private BigDecimal amount;

    @NotBlank(message = "Статус обязателен")
    private String status; // NEW, CONFIRMED и т.д.

    @NotBlank(message = "Банк отправителя обязателен")
    private String senderBank;

    @NotBlank(message = "Счет обязателен")
    private String account;

    @NotBlank(message = "Банк получателя обязателен")
    private String receiverBank;

    @Pattern(regexp = "\\d{10}|\\d{12}", message = "Некорректный ИНН")
    private String receiverInn;

    @NotBlank(message = "Расчетный счет обязателен")
    private String receiverAccount;

    @NotBlank(message = "Категория обязательна")
    private String category;

    @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Некорректный телефон")
    private String receiverPhone;

}