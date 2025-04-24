package team.mephi.hackathon.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonType personType;

    @Column(nullable = false)
    private LocalDateTime operationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(length = 500)
    private String comment;

    @Column(nullable = false, precision = 20, scale = 5)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private String senderBank;

    @Column(nullable = false)
    private String account;

    @Column(nullable = false)
    private String receiverBank;

    @Column(length = 12)
    private String receiverInn;

    @Column(nullable = false)
    private String receiverAccount;

    @Column(nullable = false)
    private String category;

    @Column(length = 20)
    private String receiverPhone;

    @Column(nullable = false)
    private boolean deleted = false;
}