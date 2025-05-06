package team.mephi.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.controller.TransactionService;
import team.mephi.hackathon.controller.ValidationService;
import team.mephi.hackathon.dto.*;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.entity.TransactionStatus;
import team.mephi.hackathon.entity.TransactionType;
import team.mephi.hackathon.exceptions.EntityNotFoundException;
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
    private final ModelMapper modelMapper;

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
                .orElseThrow(() -> new EntityNotFoundException(id.toString())));
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
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));

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
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));

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

    Transaction mapToEntity(TransactionRequestDto dto) {
        return modelMapper.map(dto, Transaction.class);
    }

    void updateEntity(Transaction entity, TransactionRequestDto dto) {
        modelMapper.map(dto, entity);
    }

    TransactionResponseDto mapToDto(Transaction entity) {
        return modelMapper.map(entity, TransactionResponseDto.class);
    }
}