package team.mephi.hackathon.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.entity.PersonType;
import team.mephi.hackathon.entity.TransactionType;
import team.mephi.hackathon.entity.TransactionStatus;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Интеграционные тесты TransactionRepository")
class TransactionRepositoryTest {

    @Container
    static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private EntityManager em;

    private Transaction buildTx(LocalDateTime opDate,
                                TransactionType type,
                                TransactionStatus status,
                                String category,
                                boolean deleted) {
        Transaction tx = new Transaction();
        tx.setEntityType("TEST");                                // обязательное поле
        tx.setPersonType(PersonType.LEGAL);
        tx.setOperationDate(opDate);
        tx.setTransactionType(type);
        tx.setAmount(BigDecimal.valueOf(100));
        tx.setStatus(status);
        tx.setSenderBank("BankA");
        tx.setAccount("AccA");
        tx.setReceiverBank("BankB");
        tx.setReceiverAccount("AccB");
        tx.setCategory(category);
        tx.setDeleted(deleted);
        return tx;
    }

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
        em.flush();
    }

    @Test
    @DisplayName("Пустая БД: findAllActive() и findAllByFilter(...) возвращают пустой список")
    void whenEmptyDatabase_thenReturnsEmpty() {
        assertThat(repository.findAllActive()).isEmpty();
        assertThat(repository.findAllByFilter(null, null, null, null, null)).isEmpty();
    }

    @Test
    @DisplayName("Флаг deleted: findAllActive() возвращает только не удалённые")
    void givenDeletedFlag_thenFindAllActiveFiltersDeleted() {
        Transaction kept = buildTx(LocalDateTime.now(), TransactionType.INCOME, TransactionStatus.NEW, "CAT", false);
        Transaction gone = buildTx(LocalDateTime.now(), TransactionType.INCOME, TransactionStatus.NEW, "CAT", true);
        em.persist(kept);
        em.persist(gone);
        em.flush();

        List<Transaction> result = repository.findAllActive();
        assertThat(result)
                .hasSize(1)
                .allMatch(tx -> !tx.isDeleted());
    }

    @Test
    @DisplayName("Полная фильтрация: по дате, типу, статусу и категории")
    void givenMultipleTransactions_whenFilterByAllCriteria_thenReturnOnlyMatches() {
        LocalDateTime now = LocalDateTime.now();
        Transaction t1 = buildTx(now.minusDays(1), TransactionType.INCOME, TransactionStatus.COMPLETED, "SALARY", false);
        Transaction t2 = buildTx(now.minusDays(10), TransactionType.OUTCOME, TransactionStatus.NEW, "BILLS", false);
        em.persist(t1);
        em.persist(t2);
        em.flush();

        // По диапазону дат (только t1)
        List<Transaction> byDate = repository.findAllByFilter(now.minusDays(2), now, null, null, null);
        assertThat(byDate).containsExactly(t1);

        // По типу
        List<Transaction> byType = repository.findAllByFilter(null, null, "INCOME", null, null);
        assertThat(byType).containsExactly(t1);

        // По статусу
        List<Transaction> byStatus = repository.findAllByFilter(null, null, null, "NEW", null);
        assertThat(byStatus).containsExactly(t2);

        // По категории
        List<Transaction> byCategory = repository.findAllByFilter(null, null, null, null, "SALARY");
        assertThat(byCategory).containsExactly(t1);

        // Совмещённый фильтр
        List<Transaction> combined = repository.findAllByFilter(
                now.minusDays(2), now, TransactionType.INCOME, TransactionStatus.COMPLETED, "SALARY"
        );
        assertThat(combined).containsExactly(t1);
    }

    @Test
    @DisplayName("Null-параметры: null-аргументы игнорируются при фильтрации")
    void whenNullFilterParameters_thenIgnored() {
        Transaction inc = buildTx(LocalDateTime.now(), TransactionType.INCOME, TransactionStatus.NEW, "X", false);
        Transaction out = buildTx(LocalDateTime.now(), TransactionType.OUTCOME, TransactionStatus.NEW, "X", false);
        em.persist(inc);
        em.persist(out);
        em.flush();

        // Только по типу
        List<Transaction> onlyIncome = repository.findAllByFilter(null, null, "INCOME", null, null);
        assertThat(onlyIncome).containsExactly(inc);

        // Только по статусу
        List<Transaction> onlyNew = repository.findAllByFilter(null, null, null, "NEW", null);
        assertThat(onlyNew).containsExactlyInAnyOrder(inc, out);

        // Без фильтров – все транзакции
        List<Transaction> all = repository.findAllByFilter(null, null, null, null, null);
        assertThat(all).containsExactlyInAnyOrder(inc, out);
    }
}
