package team.mephi.hackathon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.mephi.hackathon.dto.TransactionFilterDto;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService service;
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransactionResponseDto create(@Valid @RequestBody TransactionRequestDto dto) {
    LOGGER.debug("Received transaction: {}", dto);
    return service.createTransaction(dto);
  }

  @Operation(summary = "Get all transactions", description = "Returns a list of all transactions")
  @ApiResponse(responseCode = "200", description = "Successful operation")
  @GetMapping
  public List<TransactionResponseDto> search(
      @RequestParam(required = false) String senderBank,
      @RequestParam(required = false) String receiverBank,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime dateTo,
      @RequestParam(required = false) BigDecimal amountMin,
      @RequestParam(required = false) BigDecimal amountMax,
      @RequestParam(required = false) String category) {
    TransactionFilterDto filter = new TransactionFilterDto();
    filter.setSenderBank(senderBank);
    filter.setReceiverBank(receiverBank);
    filter.setDateFrom(dateFrom);
    filter.setDateTo(dateTo);
    filter.setAmountMin(amountMin);
    filter.setAmountMax(amountMax);
    filter.setCategory(category);
    return service.searchTransactions(filter);
  }

  @GetMapping("/{id}")
  public TransactionResponseDto getById(@PathVariable UUID id) {
    return service.getTransaction(id);
  }

  @PutMapping("/{id}")
  public TransactionResponseDto update(
      @PathVariable UUID id, @Valid @RequestBody TransactionRequestDto dto) {
    return service.updateTransaction(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    service.deleteTransaction(id);
  }
}
