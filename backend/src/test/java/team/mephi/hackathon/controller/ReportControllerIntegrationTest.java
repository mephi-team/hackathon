package team.mephi.hackathon.controller;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import team.mephi.hackathon.config.TestSecurityConfig;
import team.mephi.hackathon.entity.PersonType;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.entity.TransactionStatus;
import team.mephi.hackathon.entity.TransactionType;
import team.mephi.hackathon.repository.TransactionRepository;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestSecurityConfig.class)
class ReportControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TransactionRepository transactionRepository;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        transactionRepository.deleteAll();
    }

    private Transaction createTransaction() {
        Transaction tx = new Transaction();
        tx.setPersonType(PersonType.LEGAL);
        tx.setTransactionType(TransactionType.INCOME);
        tx.setStatus(TransactionStatus.NEW);
        tx.setAmount(BigDecimal.valueOf(1500));
        tx.setOperationDate(LocalDateTime.now());
        tx.setComment("Test transaction");
        tx.setSenderBank("AlphaBank");
        tx.setAccount("123ABC");
        tx.setReceiverBank("BetaBank");
        tx.setReceiverAccount("456DEF");
        tx.setReceiverInn("1234567890");
        tx.setCategory("SALARY");
        tx.setReceiverPhone("+79876543210");
        return tx;
    }

    @Test
    void generatePdfReport_shouldReturnValidPdf_whenTransactionsExist() {
        transactionRepository.save(createTransaction());

        byte[] responseBody = webTestClient.get()
                .uri("/api/reports/transactions/pdf")
                .accept(MediaType.APPLICATION_PDF)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().valueEquals(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-report.pdf")
                .expectBody(byte[].class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(new String(responseBody)).startsWith("%PDF");
    }

    @Test
    void generateExcelReport_shouldReturnValidExcel_whenTransactionsExist() throws Exception {
        transactionRepository.save(createTransaction());

        byte[] responseBody = webTestClient.get()
                .uri("/api/reports/transactions/excel")
                .accept(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .expectHeader().valueEquals(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-report.xlsx")
                .expectBody(byte[].class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(responseBody))) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("ID");
            assertThat(sheet.getPhysicalNumberOfRows()).isGreaterThan(1); // Должна быть хотя бы одна строка данных
        }
    }

    @Test
    void generatePdfReport_shouldReturnError_whenNoTransactionsExist() {
        webTestClient.get()
                .uri("/api/reports/transactions/pdf")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Нет активных транзакций для отчёта");
    }

    @Test
    void generateExcelReport_shouldReturnError_whenNoTransactionsExist() {
        webTestClient.get()
                .uri("/api/reports/transactions/excel")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Нет активных транзакций для отчёта");
    }
}