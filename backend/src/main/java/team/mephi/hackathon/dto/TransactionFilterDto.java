package team.mephi.hackathon.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionFilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String transactionType;
    private String status;
    private String category;
}