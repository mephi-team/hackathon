package team.mephi.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.controller.TransactionService;
import team.mephi.hackathon.controller.ValidationService;
import team.mephi.hackathon.dto.*;
import team.mephi.hackathon.entity.PersonType;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.entity.TransactionStatus;
import team.mephi.hackathon.entity.TransactionType;
import team.mephi.hackathon.exceptions.TransactionNotFoundException;
import team.mephi.hackathon.exceptions.ValidationException;
import team.mephi.hackathon.repository.TransactionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository repository;
    private final ValidationService validationService;

    public List<Transaction> getTransactions(Specification<Transaction> specification) {
        return repository.findAll(specification);
    }

    public TransactionResponseDto createTransaction(TransactionRequestDto dto) {
        validationService.validateTransaction(dto);
        Transaction entity = mapToEntity(dto);
        return mapToDto(repository.save(entity));
    }

    public TransactionResponseDto getTransaction(UUID id) {
        return mapToDto(repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id.toString())));
    }

    public List<TransactionResponseDto> searchTransactions(TransactionFilterDto filter) {
        Specification<Transaction> spec = Specification.where(null);

        if (filter.getDateFrom() != null)
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("operationDate"), filter.getDateFrom()));

        if (filter.getDateTo() != null)
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("operationDate"), filter.getDateTo()));

        if (filter.getTransactionType() != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("transactionType"),
                    TransactionType.valueOf(filter.getTransactionType())));

        if (filter.getStatus() != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"),
                    TransactionStatus.valueOf(filter.getStatus())));

        if (filter.getCategory() != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("category"), filter.getCategory()));

        if (filter.getAmountMin() != null)
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), filter.getAmountMin()));

        if (filter.getAmountMax() != null)
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("amount"), filter.getAmountMax()));

        // Фильтрация только по не удалённым
        spec = spec.and((root, q, cb) -> cb.notEqual(root.get("status"), "DELETED"));

        return repository.findAll(spec)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public TransactionResponseDto updateTransaction(UUID id, TransactionRequestDto dto) {
        Transaction entity = repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id.toString()));

        switch (entity.getStatus()) {
            case COMPLETED:
            case CONFIRMED:
            case IN_PROGRESS:
            case CANCELED:
            case DELETED:
            case REFUND:
                throw new ValidationException("Cannot update " + entity.getStatus().name() + " transaction");
        }

        validationService.validateTransaction(dto);
        updateEntity(entity, dto);

        return mapToDto(repository.save(entity));
    }

    public void deleteTransaction(UUID id) {
        Transaction entity = repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id.toString()));

        switch (entity.getStatus()) {
            case IN_PROGRESS:
            case CANCELED:
            case CONFIRMED:
            case COMPLETED:
            case REFUND:
                throw new ValidationException("Cannot delete " + entity.getStatus().name() + " transaction");
        }

        entity.setStatus(TransactionStatus.DELETED);
        repository.save(entity);
    }

    private Transaction mapToEntity(TransactionRequestDto dto) {
        Transaction entity = new Transaction();
        entity.setPersonType(PersonType.valueOf(dto.getPersonType().toUpperCase()));
        entity.setOperationDate(dto.getOperationDate());
        entity.setTransactionType(TransactionType.valueOf(dto.getTransactionType().toUpperCase()));
        entity.setComment(dto.getComment());
        entity.setAmount(dto.getAmount());
        entity.setStatus(TransactionStatus.valueOf(dto.getStatus().toUpperCase()));
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
        entity.setPersonType(PersonType.valueOf(dto.getPersonType().toUpperCase()));
        entity.setOperationDate(dto.getOperationDate());
        entity.setTransactionType(TransactionType.valueOf(dto.getTransactionType().toUpperCase()));
        entity.setComment(dto.getComment());
        entity.setAmount(dto.getAmount());
        entity.setStatus(TransactionStatus.valueOf(dto.getStatus().toUpperCase()));
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
        dto.setPersonType(entity.getPersonType().name());
        dto.setOperationDate(entity.getOperationDate());
        dto.setTransactionType(entity.getTransactionType().name());
        dto.setComment(entity.getComment());
        dto.setAmount(entity.getAmount());
        dto.setStatus(entity.getStatus().name());
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