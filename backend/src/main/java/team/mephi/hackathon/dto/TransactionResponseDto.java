package team.mephi.hackathon.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponseDto {
    private UUID id;
    private String personType;
    private LocalDateTime operationDate;
    private String transactionType;
    private String comment;
    private BigDecimal amount;
    private String status;
    private String senderBank;
    private String account;
    private String receiverBank;
    private String receiverInn;
    private String receiverAccount;
    private String category;
    private String receiverPhone;
}