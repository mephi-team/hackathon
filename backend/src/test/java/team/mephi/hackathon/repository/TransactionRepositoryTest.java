package team.mephi.hackathon.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import team.mephi.hackathon.entity.PersonType;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.entity.TransactionStatus;
import team.mephi.hackathon.entity.TransactionType;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Интеграционные тесты TransactionRepository")
class TransactionRepositoryTest {

  @Container
  static PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Autowired private TransactionRepository repository;

  @Autowired private EntityManager em;

  private Transaction buildTx(
      LocalDateTime opDate, TransactionType type, TransactionStatus status, String category) {
    Transaction tx = new Transaction();
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
    return tx;
  }

  @BeforeEach
  void cleanDatabase() {
    repository.deleteAll();
    em.flush();
  }

  // Утилита для построения спецификации
  private Specification<Transaction> filterSpec(
      LocalDateTime dateFrom,
      LocalDateTime dateTo,
      TransactionType type,
      TransactionStatus status,
      String category) {
    Specification<Transaction> spec = Specification.where(null);
    if (dateFrom != null)
      spec =
          spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("operationDate"), dateFrom));
    if (dateTo != null)
      spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("operationDate"), dateTo));
    if (type != null) spec = spec.and((root, q, cb) -> cb.equal(root.get("transactionType"), type));
    if (status != null) spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
    if (category != null)
      spec = spec.and((root, q, cb) -> cb.equal(root.get("category"), category));
    // Только не удалённые
    spec = spec.and((root, q, cb) -> cb.notEqual(root.get("status"), "DELETED"));
    return spec;
  }

  @Test
  @DisplayName("Пустая БД: findAllActive() и findAll(spec) возвращают пустой список")
  void whenEmptyDatabase_thenReturnsEmpty() {
    assertThat(repository.findAllActive()).isEmpty();
    assertThat(repository.findAll(filterSpec(null, null, null, null, null))).isEmpty();
  }

  @Test
  @DisplayName("Флаг deleted: findAllActive() возвращает только не удалённые")
  void givenDeletedFlag_thenFindAllActiveFiltersDeleted() {
    Transaction kept =
        buildTx(LocalDateTime.now(), TransactionType.INCOME, TransactionStatus.NEW, "CAT");
    Transaction gone =
        buildTx(LocalDateTime.now(), TransactionType.INCOME, TransactionStatus.DELETED, "CAT");
    em.persist(kept);
    em.persist(gone);
    em.flush();

    List<Transaction> result = repository.findAllActive();
    assertThat(result).hasSize(1).allMatch(tx -> !tx.getStatus().equals(TransactionStatus.DELETED));
  }

  @Test
  @DisplayName("Полная фильтрация: по дате, типу, статусу и категории")
  void givenMultipleTransactions_whenFilterByAllCriteria_thenReturnOnlyMatches() {
    LocalDateTime now = LocalDateTime.now();
    Transaction t1 =
        buildTx(now.minusDays(1), TransactionType.INCOME, TransactionStatus.COMPLETED, "SALARY");
    Transaction t2 =
        buildTx(now.minusDays(10), TransactionType.OUTCOME, TransactionStatus.NEW, "BILLS");
    em.persist(t1);
    em.persist(t2);
    em.flush();

    // По диапазону дат (только t1)
    List<Transaction> byDate =
        repository.findAll(filterSpec(now.minusDays(2), now, null, null, null));
    assertThat(byDate).containsExactly(t1);

    // По типу
    List<Transaction> byType =
        repository.findAll(filterSpec(null, null, TransactionType.INCOME, null, null));
    assertThat(byType).containsExactly(t1);

    // По статусу
    List<Transaction> byStatus =
        repository.findAll(filterSpec(null, null, null, TransactionStatus.NEW, null));
    assertThat(byStatus).containsExactly(t2);

    // По категории
    List<Transaction> byCategory = repository.findAll(filterSpec(null, null, null, null, "SALARY"));
    assertThat(byCategory).containsExactly(t1);

    // Совмещённый фильтр
    List<Transaction> combined =
        repository.findAll(
            filterSpec(
                now.minusDays(2),
                now,
                TransactionType.INCOME,
                TransactionStatus.COMPLETED,
                "SALARY"));
    assertThat(combined).containsExactly(t1);
  }

  @Test
  @DisplayName("Null-параметры: null-аргументы игнорируются при фильтрации")
  void whenNullFilterParameters_thenIgnored() {
    Transaction inc =
        buildTx(LocalDateTime.now(), TransactionType.INCOME, TransactionStatus.NEW, "X");
    Transaction out =
        buildTx(LocalDateTime.now(), TransactionType.OUTCOME, TransactionStatus.NEW, "X");
    em.persist(inc);
    em.persist(out);
    em.flush();

    // Только по типу
    List<Transaction> onlyIncome =
        repository.findAll(filterSpec(null, null, TransactionType.INCOME, null, null));
    assertThat(onlyIncome).containsExactly(inc);

    // Только по статусу
    List<Transaction> onlyNew =
        repository.findAll(filterSpec(null, null, null, TransactionStatus.NEW, null));
    assertThat(onlyNew).containsExactlyInAnyOrder(inc, out);

    // Без фильтров – все транзакции
    List<Transaction> all = repository.findAll(filterSpec(null, null, null, null, null));
    assertThat(all).containsExactlyInAnyOrder(inc, out);
  }
}
