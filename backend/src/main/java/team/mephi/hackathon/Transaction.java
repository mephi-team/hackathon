package team.mephi.hackathon.model;

import lombok.Data;
import java.util.UUID;

@Data
public class Transaction {
    private UUID id;
    private double amount;
    private String currency;
    private String description;
    private boolean deleted = false; // Новое поле для статуса
}