package team.mephi.hackathon.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponseDto {
    private UUID id;
    private double amount;
    private String currency;
    private String description;
    private LocalDateTime createdAt;
}