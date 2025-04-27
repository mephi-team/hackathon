package team.mephi.hackathon.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.repository.TransactionRepository;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReportService reportService;

    @MockBean
    private TransactionRepository transactionRepository;

    @Test
    void generatePdfReport_ShouldReturnPdf() throws java.io.IOException {
        Mockito.when(transactionRepository.findAllActive()).thenReturn(List.of(new Transaction()));
        Mockito.when(reportService.generatePdfReport(any())).thenReturn("%PDF-1.4".getBytes());

        webTestClient.get().uri("/api/reports/transactions/pdf")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().valueMatches("Content-Disposition", ".*transactions-report\\.pdf.*");
    }

    @Test
    void generatePdfReport_NotFound() throws java.io.IOException {
        Mockito.when(transactionRepository.findAllActive()).thenReturn(Collections.emptyList());

        webTestClient.get().uri("/api/reports/transactions/pdf")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void generateExcelReport_ShouldReturnExcel() throws java.io.IOException {
        Mockito.when(transactionRepository.findAllActive()).thenReturn(List.of(new Transaction()));
        Mockito.when(reportService.generateExcelReport(any())).thenReturn(new byte[]{0x50, 0x4B});

        webTestClient.get().uri("/api/reports/transactions/excel")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .expectHeader().valueMatches("Content-Disposition", ".*transactions-report\\.xlsx.*");
    }

    @Test
    void generateExcelReport_NotFound() throws java.io.IOException {
        Mockito.when(transactionRepository.findAllActive()).thenReturn(Collections.emptyList());

        webTestClient.get().uri("/api/reports/transactions/excel")
                .exchange()
                .expectStatus().isNotFound();
    }
}