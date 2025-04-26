package team.mephi.hackathon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.mephi.hackathon.exceptions.NoTransactionsFoundException;
import team.mephi.hackathon.repository.TransactionRepository;
import team.mephi.hackathon.service.ReportService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final TransactionRepository transactionRepository;
    private final ReportService reportService;

    @GetMapping(value = "/transactions/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdfReport() throws IOException {
        List<Transaction> transactions = transactionRepository.findAllActive();
        if (transactions.isEmpty()) {
            throw new NoTransactionsFoundException("No active transactions found");
        }

        byte[] pdf = reportService.generatePdfReport(transactions);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-report.pdf")
                .body(pdf);
    }

    @GetMapping(value = "/transactions/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> generateExcelReport() throws IOException {
        List<Transaction> transactions = transactionRepository.findAllActive();
        if (transactions.isEmpty()) {
            throw new NoTransactionsFoundException("No active transactions found");
        }

        byte[] excel = reportService.generateExcelReport(transactions);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-report.xlsx")
                .body(excel);
    }
}