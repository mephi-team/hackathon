package team.mephi.hackathon.service;

import team.mephi.hackathon.entity.Transaction;

import java.io.IOException;
import java.util.List;

/**
 * Сервис генерации отчётов по транзакциям.
 * Колонки в отчётах полностью соответствуют полям,
 * описанным в {@link team.mephi.hackathon.dto.TransactionRequestDto}.
 */
public interface ReportService {

    /**
     * Создаёт PDF-отчёт по списку транзакций.
     *
     * @param transactions список активных транзакций
     * @return содержимое сформированного PDF-файла
     */
    byte[] generatePdfReport(List<Transaction> transactions) throws IOException;

    /**
     * Создаёт Excel-отчёт по списку транзакций.
     *
     * @param transactions список активных транзакций
     * @return содержимое сформированного XLSX-файла
     */
    byte[] generateExcelReport(List<Transaction> transactions) throws IOException;
}