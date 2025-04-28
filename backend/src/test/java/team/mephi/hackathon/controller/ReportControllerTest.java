package team.mephi.hackathon.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.repository.TransactionRepository;
import team.mephi.hackathon.controller.ReportService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {
    @Mock
    private ReportService reportServiceImpl;
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ReportService reportService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        ReportController controller = new ReportController(transactionRepository, reportService);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void generatePdfReport_shouldReturnPdfBytes_whenTransactionsExist() throws IOException {
        Transaction tx = new Transaction();
        when(transactionRepository.findAllActive()).thenReturn(List.of(tx));
        byte[] expected = new byte[]{1, 2, 3};
        when(reportService.generatePdfReport(any())).thenReturn(expected);

        webTestClient.get()
                .uri("/api/reports/transactions/pdf")
                .accept(MediaType.APPLICATION_PDF)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().valueEquals(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-report.pdf")
                .expectBody(byte[].class).isEqualTo(expected);
    }

    @Test
    void generatePdfReport_shouldReturnServerError_whenNoTransactions() {
        when(transactionRepository.findAllActive()).thenReturn(Collections.emptyList());

        webTestClient.get()
                .uri("/api/reports/transactions/pdf")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void generateExcelReport_shouldReturnExcelBytes_whenTransactionsExist() throws IOException {
        Transaction tx = new Transaction();
        when(transactionRepository.findAllActive()).thenReturn(List.of(tx));
        byte[] expected = new byte[]{4, 5, 6};
        when(reportService.generateExcelReport(any())).thenReturn(expected);

        webTestClient.get()
                .uri("/api/reports/transactions/excel")
                .accept(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .expectHeader().valueEquals(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-report.xlsx")
                .expectBody(byte[].class).isEqualTo(expected);
    }

    @Test
    void generateExcelReport_shouldReturnServerError_whenNoTransactions() {
        when(transactionRepository.findAllActive()).thenReturn(Collections.emptyList());

        webTestClient.get()
                .uri("/api/reports/transactions/excel")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
