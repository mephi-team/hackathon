package team.mephi.hackathon.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionDto {
    @Positive(message = "Amount must be positive")
    private double amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;

    @Size(max = 255, message = "Description too long")
    private String description;
}