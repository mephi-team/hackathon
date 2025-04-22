package team.mephi.hackathon.model;

import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;
    @Column(nullable = false)
    private double amount;
    @Column(nullable = false, length = 3)
    private String currency;
    @Column(length = 255)
    private String description;
    @Column(nullable = false)
    private boolean deleted = false;
    private UUID id;
    private double amount;
    private String currency;
    private String description;
    private boolean deleted = false; // Новое поле для статуса
    // Добавим геттер для deleted (необходим для PDFBox)
    public boolean isDeleted() {
        return deleted;
    }
}