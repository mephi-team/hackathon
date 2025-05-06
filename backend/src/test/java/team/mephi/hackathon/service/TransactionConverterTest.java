package team.mephi.hackathon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.entity.PersonType;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.entity.TransactionStatus;
import team.mephi.hackathon.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionConverterTest {
    private TransactionRequestDto dto;
    private Transaction entity;
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        dto = new TransactionRequestDto();
        dto.setPersonType("LEGAL");
        dto.setTransactionType("INCOME");
        dto.setStatus("NEW");
        dto.setAmount(BigDecimal.valueOf(1000));
        dto.setOperationDate(LocalDateTime.now());
        dto.setComment("Salary");
        dto.setSenderBank("AlphaBank");
        dto.setAccount("ACC123");
        dto.setReceiverBank("BetaBank");
        dto.setReceiverAccount("REC456");
        dto.setReceiverInn("1234567890");
        dto.setCategory("SALARY");
        dto.setReceiverPhone("+79876543210");

        entity = new Transaction();
        entity.setId(UUID.randomUUID());
        entity.setPersonType(PersonType.LEGAL);
        entity.setTransactionType(TransactionType.INCOME);
        entity.setStatus(TransactionStatus.NEW);
        entity.setAmount(BigDecimal.valueOf(1000));
        entity.setOperationDate(LocalDateTime.now().minusDays(1));
        entity.setComment("Old salary");
        entity.setSenderBank("OldBank");
        entity.setAccount("OLD123");
        entity.setReceiverBank("OldBeta");
        entity.setReceiverAccount("OLD456");
        entity.setReceiverInn("0987654321");
        entity.setCategory("OLD_SALARY");
        entity.setReceiverPhone("80000000000");

        modelMapper = new ModelMapper();
    }

    @Test
    void mapToEntity_shouldConvertAllFieldsCorrectly() {
        Transaction result = modelMapper.map(dto, Transaction.class);

        assertThat(result).isNotNull();

        assertThat(result.getPersonType()).isEqualTo(PersonType.LEGAL);
        assertThat(result.getTransactionType()).isEqualTo(TransactionType.INCOME);
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.NEW);
        assertThat(result.getAmount()).isEqualTo(dto.getAmount());
        assertThat(result.getOperationDate()).isEqualTo(dto.getOperationDate());
        assertThat(result.getComment()).isEqualTo(dto.getComment());
        assertThat(result.getSenderBank()).isEqualTo(dto.getSenderBank());
        assertThat(result.getAccount()).isEqualTo(dto.getAccount());
        assertThat(result.getReceiverBank()).isEqualTo(dto.getReceiverBank());
        assertThat(result.getReceiverAccount()).isEqualTo(dto.getReceiverAccount());
        assertThat(result.getReceiverInn()).isEqualTo(dto.getReceiverInn());
        assertThat(result.getCategory()).isEqualTo(dto.getCategory());
        assertThat(result.getReceiverPhone()).isEqualTo(dto.getReceiverPhone());
    }

    @Test
    void mapToDto_shouldConvertAllFieldsCorrectly() {
        TransactionResponseDto result = modelMapper.map(entity, TransactionResponseDto.class);

        assertThat(result).isNotNull();

        assertThat(result.getPersonType()).isEqualTo("LEGAL");
        assertThat(result.getTransactionType()).isEqualTo("INCOME");
        assertThat(result.getStatus()).isEqualTo("NEW");
        assertThat(result.getAmount()).isEqualTo(entity.getAmount());
        assertThat(result.getOperationDate()).isEqualTo(entity.getOperationDate());
        assertThat(result.getComment()).isEqualTo(entity.getComment());
        assertThat(result.getSenderBank()).isEqualTo(entity.getSenderBank());
        assertThat(result.getAccount()).isEqualTo(entity.getAccount());
        assertThat(result.getReceiverBank()).isEqualTo(entity.getReceiverBank());
        assertThat(result.getReceiverAccount()).isEqualTo(entity.getReceiverAccount());
        assertThat(result.getReceiverInn()).isEqualTo(entity.getReceiverInn());
        assertThat(result.getCategory()).isEqualTo(entity.getCategory());
        assertThat(result.getReceiverPhone()).isEqualTo(entity.getReceiverPhone());
    }

    @Test
    void updateEntity_shouldReplaceAllFieldsCorrectly() {
        Transaction updatedEntity = new Transaction();
        modelMapper.map(dto, updatedEntity);

        assertThat(updatedEntity.getPersonType()).isEqualTo(PersonType.LEGAL);
        assertThat(updatedEntity.getTransactionType()).isEqualTo(TransactionType.INCOME);
        assertThat(updatedEntity.getStatus()).isEqualTo(TransactionStatus.NEW);
        assertThat(updatedEntity.getAmount()).isEqualTo(dto.getAmount());
        assertThat(updatedEntity.getOperationDate()).isEqualTo(dto.getOperationDate());
        assertThat(updatedEntity.getComment()).isEqualTo(dto.getComment());
        assertThat(updatedEntity.getSenderBank()).isEqualTo(dto.getSenderBank());
        assertThat(updatedEntity.getAccount()).isEqualTo(dto.getAccount());
        assertThat(updatedEntity.getReceiverBank()).isEqualTo(dto.getReceiverBank());
        assertThat(updatedEntity.getReceiverAccount()).isEqualTo(dto.getReceiverAccount());
        assertThat(updatedEntity.getReceiverInn()).isEqualTo(dto.getReceiverInn());
        assertThat(updatedEntity.getCategory()).isEqualTo(dto.getCategory());
        assertThat(updatedEntity.getReceiverPhone()).isEqualTo(dto.getReceiverPhone());
    }

    @Test
    void mapToEntity_withNullFields_shouldHandleGracefully() {
        TransactionRequestDto emptyDto = new TransactionRequestDto();
        Transaction result = modelMapper.map(emptyDto, Transaction.class);

        assertThat(result.getPersonType()).isNull();
        assertThat(result.getTransactionType()).isNull();
        assertThat(result.getStatus()).isNull();
        assertThat(result.getAmount()).isNull();
        assertThat(result.getOperationDate()).isNull();
        assertThat(result.getComment()).isNull();
        assertThat(result.getSenderBank()).isNull();
        assertThat(result.getAccount()).isNull();
        assertThat(result.getReceiverBank()).isNull();
        assertThat(result.getReceiverAccount()).isNull();
        assertThat(result.getReceiverInn()).isNull();
        assertThat(result.getCategory()).isNull();
        assertThat(result.getReceiverPhone()).isNull();
    }
}