package team.mephi.hackathon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterDto {
    private String senderBank;
    private String receiverBank;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private BigDecimal amountMin;
    private BigDecimal amountMax;
    private String category;
}