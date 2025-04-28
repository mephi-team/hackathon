package team.mephi.hackathon.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import team.mephi.hackathon.config.TestSecurityConfig;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class TransactionControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanup() {
        jdbcTemplate.execute("TRUNCATE TABLE transactions RESTART IDENTITY CASCADE");
    }

    private TransactionRequestDto createRequest(BigDecimal amount, String category, String senderBank) {
        TransactionRequestDto dto = new TransactionRequestDto();
        dto.setPersonType("LEGAL");
        dto.setOperationDate(LocalDateTime.now());
        dto.setTransactionType("INCOME");
        dto.setAmount(amount);
        dto.setStatus("NEW");
        dto.setSenderBank(senderBank);
        dto.setAccount("123");
        dto.setReceiverBank("RB");
        dto.setReceiverAccount("456");
        dto.setCategory(category);
        dto.setReceiverInn("1234567890");
        dto.setReceiverPhone("+71234567890");
        return dto;
    }

    @Test
    void crudFlow() {
        // Create
        TransactionRequestDto request = createRequest(BigDecimal.valueOf(100), "CAT", "SBP");
        TransactionResponseDto created = webTestClient.post()
                .uri("/api/transactions")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(created).isNotNull();
        UUID id = created.getId();

        // Get
        TransactionResponseDto fetched = webTestClient.get()
                .uri("/api/transactions/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(fetched).isNotNull();
        assertThat(fetched.getAmount()).isEqualTo(request.getAmount());

        // Update
        request.setAmount(BigDecimal.valueOf(200));
        TransactionResponseDto updated = webTestClient.put()
                .uri("/api/transactions/{id}", id)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(updated).isNotNull();
        assertThat(updated.getAmount()).isEqualTo(BigDecimal.valueOf(200));
    }

    @Test
    void searchAndFilter() {
        // Prepare
        createTransaction(BigDecimal.valueOf(50), "CAT1", "BankA");
        createTransaction(BigDecimal.valueOf(150), "CAT2", "BankB");

        // Filter by amount
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/transactions/transactions")
                        .queryParam("amountMin", "100")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(1);

        // Filter by category
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/transactions/transactions")
                        .queryParam("category", "CAT1")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(1);
    }

    @Test
    void validationErrors() {
        // Missing operationDate
        TransactionRequestDto invalid = createRequest(BigDecimal.valueOf(100), "CAT", "SBP");
        invalid.setOperationDate(null);

        webTestClient.post()
                .uri("/api/transactions")
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("operationDate").exists();

        // Invalid date format
        webTestClient.get()
                .uri("/api/transactions/transactions?dateFrom=invalid")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void nonExistingId_returns404() {
        UUID randomId = UUID.randomUUID();

        webTestClient.get()
                .uri("/api/transactions/{id}", randomId)
                .exchange()
                .expectStatus().isNotFound();

        webTestClient.put()
                .uri("/api/transactions/{id}", randomId)
                .bodyValue(createRequest(BigDecimal.valueOf(100), "CAT", "SBP"))
                .exchange()
                .expectStatus().isNotFound();

        webTestClient.delete()
                .uri("/api/transactions/{id}", randomId)
                .exchange()
                .expectStatus().isNotFound();
    }

    private void createTransaction(BigDecimal amount, String category, String senderBank) {
        webTestClient.post()
                .uri("/api/transactions")
                .bodyValue(createRequest(amount, category, senderBank))
                .exchange()
                .expectStatus().isCreated();
    }
}
