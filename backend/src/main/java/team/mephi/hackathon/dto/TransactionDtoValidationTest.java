package team.mephi.hackathon.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class TransactionDtoValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createTransaction_InvalidAmount_ShouldReturnBadRequest() throws Exception {
        String json = "{ \"amount\": -50, \"currency\": \"USD\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTransaction_InvalidCurrency_ShouldReturnBadRequest() throws Exception {
        String json = "{ \"amount\": 100, \"currency\": \"US\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}