package team.mephi.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.dto.*;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.exception.TransactionNotFoundException;
import team.mephi.hackathon.repository.TransactionRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final ValidationService validationService;

    public List<Transaction> getTransactions(Specification<Transaction> specification) {
        return repository.findAll(specification);
    }

    @Transactional
    public TransactionResponseDto createTransaction(TransactionRequestDto dto) {
        validationService.validateTransaction(dto);
        Transaction entity = mapToEntity(dto);
        return mapToDto(repository.save(entity));
    }

    public TransactionResponseDto getTransaction(Long id) {
        return mapToDto(repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id)));
    }

    public List<TransactionResponseDto> searchTransactions(TransactionFilterDto filter) {
        return repository.findAllByFilter(filter).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponseDto updateTransaction(Long id, TransactionRequestDto dto) {
        Transaction entity = repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        validationService.validateTransaction(dto);
        updateEntity(entity, dto);

        return mapToDto(repository.save(entity));
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction entity = repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        entity.setDeleted(true);
        repository.save(entity);
    }

    private Transaction mapToEntity(TransactionRequestDto dto) {
        Transaction entity = new Transaction();
        entity.setPersonType(dto.getPersonType());
        entity.setOperationDate(dto.getOperationDate());
        entity.setTransactionType(dto.getTransactionType());
        entity.setComment(dto.getComment());
        entity.setAmount(dto.getAmount());
        entity.setStatus(dto.getStatus());
        entity.setSenderBank(dto.getSenderBank());
        entity.setAccount(dto.getAccount());
        entity.setReceiverBank(dto.getReceiverBank());
        entity.setReceiverInn(dto.getReceiverInn());
        entity.setReceiverAccount(dto.getReceiverAccount());
        entity.setCategory(dto.getCategory());
        entity.setReceiverPhone(dto.getReceiverPhone());
        return entity;
    }

    private void updateEntity(Transaction entity, TransactionRequestDto dto) {
        entity.setPersonType(dto.getPersonType());
        entity.setOperationDate(dto.getOperationDate());
        entity.setTransactionType(dto.getTransactionType());
        entity.setComment(dto.getComment());
        entity.setAmount(dto.getAmount());
        entity.setStatus(dto.getStatus());
        entity.setSenderBank(dto.getSenderBank());
        entity.setAccount(dto.getAccount());
        entity.setReceiverBank(dto.getReceiverBank());
        entity.setReceiverInn(dto.getReceiverInn());
        entity.setReceiverAccount(dto.getReceiverAccount());
        entity.setCategory(dto.getCategory());
        entity.setReceiverPhone(dto.getReceiverPhone());
    }

    private TransactionResponseDto mapToDto(Transaction entity) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(entity.getId());
        dto.setPersonType(entity.getPersonType());
        dto.setOperationDate(entity.getOperationDate());
        dto.setTransactionType(entity.getTransactionType());
        dto.setComment(entity.getComment());
        dto.setAmount(entity.getAmount());
        dto.setStatus(entity.getStatus());
        dto.setSenderBank(entity.getSenderBank());
        dto.setAccount(entity.getAccount());
        dto.setReceiverBank(entity.getReceiverBank());
        dto.setReceiverInn(entity.getReceiverInn());
        dto.setReceiverAccount(entity.getReceiverAccount());
        dto.setCategory(entity.getCategory());
        dto.setReceiverPhone(entity.getReceiverPhone());
        return dto;
    }
}