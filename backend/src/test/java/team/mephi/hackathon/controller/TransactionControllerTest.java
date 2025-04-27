package team.mephi.hackathon.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.exceptions.TransactionNotFoundException;
import team.mephi.hackathon.service.TransactionService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class TransactionControllerTest {

    private WebTestClient webTestClient;
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        transactionService = mock(TransactionService.class);
        TransactionController controller = new TransactionController(transactionService);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void createTransaction_ShouldReturnCreated() {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setPersonType("LEGAL");
        request.setTransactionType("INCOME");
        request.setStatus("COMPLETED");
        request.setAmount(BigDecimal.valueOf(1000));

        TransactionResponseDto response = new TransactionResponseDto();
        response.setId(UUID.randomUUID());

        Mockito.when(transactionService.createTransaction(any())).thenReturn(response);

        webTestClient.post().uri("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void getTransaction_ShouldReturnOk() {
        UUID id = UUID.randomUUID();
        TransactionResponseDto response = new TransactionResponseDto();
        response.setId(id);

        Mockito.when(transactionService.getTransaction(id)).thenReturn(response);

        webTestClient.get().uri("/api/transactions/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id.toString());
    }

    @Test
    void getTransaction_NotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(transactionService.getTransaction(id)).thenThrow(new TransactionNotFoundException(id.toString()));

        webTestClient.get().uri("/api/transactions/" + id)
                .exchange()
                .expectStatus().isNotFound();
    }
}