package team.mephi.hackathon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import team.mephi.hackathon.dto.TransactionDto;
import team.mephi.hackathon.model.Transaction;
import team.mephi.hackathon.service.TransactionService;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.dto.TransactionDto;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UUID testId = UUID.randomUUID();
    private final TransactionDto testDto = new TransactionDto(100.0, "USD", "Test");
    private final Transaction testTransaction = Transaction.builder()
            .id(testId)
            .amount(100.0)
            .currency("USD")
            .description("Test")
            .build();

    @Test
    void createTransaction_ShouldReturnCreated() throws Exception {
        when(transactionService.createTransaction(any(TransactionDto.class))).thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.description").value("Test"));

        verify(transactionService).createTransaction(any(TransactionDto.class)));
    }

    @Test
    void getTransactions_ShouldReturnOk() throws Exception {
        when(transactionService.getFilteredTransactions(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(testTransaction));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testId.toString()))
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].currency").value("USD"))
                .andExpect(jsonPath("$[0].description").value("Test"));
    }

    @Test
    void updateTransaction_ShouldReturnOk() throws Exception {
        when(transactionService.updateTransaction(eq(testId), any(TransactionDto.class)))
                .thenReturn(testTransaction);

        mockMvc.perform(put("/api/transactions/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void updateTransaction_NotFound_ShouldReturn404() throws Exception {
        when(transactionService.updateTransaction(eq(testId), any(TransactionDto.class)))
                .thenThrow(new TransactionNotFoundException("Not found"));

        mockMvc.perform(put("/api/transactions/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found"));
    }

    @Test
    void deleteTransaction_ShouldReturnNoContent() throws Exception {
        doNothing().when(transactionService).deleteTransaction(testId);

        mockMvc.perform(delete("/api/transactions/" + testId))
                .andExpect(status().isNoContent());

        verify(transactionService).deleteTransaction(testId);
    }

    @Test
    void deleteTransaction_NotFound_ShouldReturn404() throws Exception {
        doThrow(new TransactionNotFoundException("Not found"))
                .when(transactionService).deleteTransaction(testId);

        mockMvc.perform(delete("/api/transactions/" + testId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found"));
    }
}