package team.mephi.hackathon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

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
}
