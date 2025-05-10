package team.mephi.hackathon.controller;

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

/**
 * REST-контроллер для скачивания отчётов по транзакциям. Формат отчётов отражает все поля из {@link
 * team.mephi.hackathon.dto.TransactionRequestDto}.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

  private final TransactionRepository transactionRepository;
  private final ReportService reportService;

  /** Сформировать и отдать PDF-отчёт. */
  @GetMapping(value = "/transactions/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
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

  /** Сформировать и отдать Excel-отчёт. */
  @GetMapping(
      value = "/transactions/excel",
      produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
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
