package team.mephi.hackathon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.exceptions.EntityNotFoundException;
import team.mephi.hackathon.repository.TransactionRepository;

/** REST-контроллер для генерации отчётов по транзакциям. Поддерживает форматы: PDF и Excel. */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Отчёты", description = "API для генерации отчётов по транзакциям")
public class ReportController {
  /**
   * Сервис для работы с транзакциями. Используется для получения данных перед генерацией отчёта.
   */
  private final TransactionRepository transactionRepository;

  /** Сервис для генерации отчётов. Инжектируется через конструктор. */
  private final ReportService reportService;

  /**
   * Генерирует PDF-отчёт по транзакциям за указанный период.
   *
   * @return PDF-файл с данными о транзакциях
   */
  @GetMapping(value = "/transactions/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  @Operation(
      summary = "Скачать PDF-отчёт",
      description = "Генерирует и возвращает PDF-файл с транзакциями за указанный период",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "PDF-отчёт успешно сгенерирован",
            content = @Content(mediaType = "application/pdf")),
        @ApiResponse(
            responseCode = "404",
            description = "Нет активных транзакций для отчёта",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Ошибка при генерации отчёта",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  public ResponseEntity<byte[]> generatePdfReport() throws IOException {
    List<Transaction> transactions = transactionRepository.findAllActive();

    if (transactions.isEmpty()) {
      throw new EntityNotFoundException("Нет активных транзакций для отчёта");
    }

    byte[] pdfBytes = reportService.generatePdfReport(transactions);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-report.pdf")
        .body(pdfBytes);
  }

  /**
   * Генерирует Excel-отчёт по транзакциям за указанный период.
   *
   * @return XLSX-файл с данными о транзакциях
   */
  @GetMapping(
      value = "/transactions/excel",
      produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  @Operation(
      summary = "Скачать Excel-отчёт",
      description = "Генерирует и возвращает Excel-файл с транзакциями за указанный период",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Excel-отчёт успешно сгенерирован",
            content =
                @Content(
                    mediaType =
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
        @ApiResponse(
            responseCode = "404",
            description = "Нет активных транзакций для отчёта",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Ошибка при генерации отчёта",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  public ResponseEntity<byte[]> generateExcelReport() throws IOException {
    List<Transaction> transactions = transactionRepository.findAllActive();

    if (transactions.isEmpty()) {
      throw new EntityNotFoundException("Нет активных транзакций для отчёта");
    }

    byte[] excelBytes = reportService.generateExcelReport(transactions);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-report.xlsx")
        .body(excelBytes);
  }
}
