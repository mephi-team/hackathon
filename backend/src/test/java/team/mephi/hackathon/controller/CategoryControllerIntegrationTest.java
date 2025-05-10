package team.mephi.hackathon.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import team.mephi.hackathon.config.TestSecurityConfig;
import team.mephi.hackathon.dto.CategoryRequestDto;
import team.mephi.hackathon.dto.CategoryResponseDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired private WebTestClient webTestClient;

  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void cleanup() {
    jdbcTemplate.execute("TRUNCATE TABLE categories RESTART IDENTITY CASCADE");
  }

  private CategoryRequestDto createRequest(String name) {
    return new CategoryRequestDto(name);
  }

  @Test
  void crudFlow() {
    // Create
    CategoryRequestDto request = createRequest("SALARY");
    CategoryResponseDto created =
        webTestClient
            .post()
            .uri("/api/categories")
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CategoryResponseDto.class)
            .returnResult()
            .getResponseBody();

    assertThat(created).isNotNull();
    UUID id = created.getId();

    // Update
    request = createRequest("UTILITY");
    CategoryResponseDto updated =
        webTestClient
            .put()
            .uri("/api/categories/{id}", id)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(CategoryResponseDto.class)
            .returnResult()
            .getResponseBody();

    assertThat(updated).isNotNull();
    assertThat(updated.getName()).isEqualTo("UTILITY");
  }

  @Test
  void validationErrors() {
    // Missing name
    CategoryRequestDto invalid = createRequest(null);

    webTestClient
        .post()
        .uri("/api/categories")
        .bodyValue(invalid)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("name")
        .exists();
  }

  @Test
  void nonExistingId_returns404() {
    UUID randomId = UUID.randomUUID();

    webTestClient
        .put()
        .uri("/api/categories/{id}", randomId)
        .bodyValue(createRequest("UNKNOWN"))
        .exchange()
        .expectStatus()
        .isNotFound();

    webTestClient
        .delete()
        .uri("/api/categories/{id}", randomId)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void getAllCategories() {
    // Prepare
    createCategory("SALARY");
    createCategory("UTILITY");

    // Get all
    webTestClient
        .get()
        .uri("/api/categories")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(CategoryResponseDto.class)
        .hasSize(2);
  }

  private void createCategory(String name) {
    webTestClient
        .post()
        .uri("/api/categories")
        .bodyValue(createRequest(name))
        .exchange()
        .expectStatus()
        .isCreated();
  }
}
