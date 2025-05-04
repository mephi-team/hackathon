package team.mephi.hackathon.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerUnitTest {

    @Mock
    private TransactionService transactionService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        TransactionController controller = new TransactionController(transactionService);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void createTransaction_shouldReturnCreatedDto() {
        UUID id = UUID.randomUUID();
        TransactionResponseDto resp = new TransactionResponseDto();
        resp.setId(id);
        when(transactionService.createTransaction(any(TransactionRequestDto.class)))
                .thenReturn(resp);

        TransactionRequestDto req = new TransactionRequestDto();
        req.setPersonType("LEGAL");
        req.setOperationDate(LocalDateTime.now());
        req.setTransactionType("INCOME");
        req.setAmount(BigDecimal.valueOf(100));
        req.setStatus("NEW");
        req.setSenderBank("SBP");
        req.setAccount("123");
        req.setReceiverBank("RB");
        req.setReceiverAccount("456");
        req.setCategory("CAT");
        req.setReceiverInn("1234567890");
        req.setReceiverPhone("+71234567890");

        webTestClient.post()
                .uri("/api/transactions")
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionResponseDto.class)
                .value(response -> assertEquals(id, response.getId()));
    }

    @Test
    void getById_shouldReturnDto() {
        UUID id = UUID.randomUUID();
        TransactionResponseDto resp = new TransactionResponseDto();
        resp.setId(id);
        when(transactionService.getTransaction(id)).thenReturn(resp);

        webTestClient.get()
                .uri("/api/transactions/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDto.class)
                .value(response -> assertEquals(id, response.getId()));
    }

    @Test
    void updateTransaction_shouldReturnUpdatedDto() {
        UUID id = UUID.randomUUID();
        TransactionResponseDto resp = new TransactionResponseDto();
        resp.setId(id);
        when(transactionService.updateTransaction(eq(id), any(TransactionRequestDto.class)))
                .thenReturn(resp);

        TransactionRequestDto req = new TransactionRequestDto();
        req.setPersonType("LEGAL");
        req.setOperationDate(LocalDateTime.now());
        req.setTransactionType("INCOME");
        req.setAmount(BigDecimal.valueOf(200));
        req.setStatus("COMPLETED");
        req.setSenderBank("SBP");
        req.setAccount("789");
        req.setReceiverBank("RB");
        req.setReceiverAccount("012");
        req.setCategory("CAT2");
        req.setReceiverInn("123456789012");
        req.setReceiverPhone("81234567890");

        webTestClient.put()
                .uri("/api/transactions/{id}", id)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDto.class)
                .value(response -> assertEquals(id, response.getId()));
    }

    @Test
    void deleteTransaction_shouldReturnNoContent() {
        UUID id = UUID.randomUUID();
        doNothing().when(transactionService).deleteTransaction(id);

        webTestClient.delete()
                .uri("/api/transactions/{id}", id)
                .exchange()
                .expectStatus().isNoContent();

        verify(transactionService).deleteTransaction(id);
    }

    @Test
    void searchTransactions_shouldReturnList() {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(UUID.randomUUID());
        when(transactionService.searchTransactions(any())).thenReturn(List.of(dto));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/transactions")
                        .queryParam("status", "NEW")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionResponseDto.class)
                .hasSize(1);
    }

    @Test
    void getTransactions_withParams_shouldReturnList() {
        Transaction tx = new Transaction();
        UUID id = UUID.randomUUID();
        tx.setId(id);
        when(transactionService.getTransactions(any())).thenReturn(List.of(tx));

        LocalDateTime now = LocalDateTime.now();
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/transactions")
                        .queryParam("senderBank", "SB")
                        .queryParam("receiverBank", "RB")
                        .queryParam("dateFrom", now.minusDays(1).toString())
                        .queryParam("dateTo", now.toString())
                        .queryParam("amountMin", "10")
                        .queryParam("amountMax", "100")
                        .queryParam("category", "CAT")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Transaction.class)
                .hasSize(1)
                .value(list -> assertEquals(id, list.get(0).getId()));

        verify(transactionService).getTransactions(any());
    }

    @Test
    void getTransactions_noParams_shouldReturnEmptyList() {
        when(transactionService.getTransactions(any())).thenReturn(List.of());

        webTestClient.get()
                .uri("/api/transactions/transactions")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Transaction.class)
                .hasSize(0);

        verify(transactionService).getTransactions(any());
    }
}
