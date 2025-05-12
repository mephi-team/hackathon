# Описание проекта: Spring Boot + Keycloak + PostgreSQL + React

[![CI](https://github.com/mephi-team/hackathon/workflows/CI%20Pipeline/badge.svg)](https://github.com/mephi-team/hackathon/actions/workflows/maven-test.yml)

## 1. Диаграммы и архитектура

Все диаграммы из презентации располагаются в файле [hakathon_architecture.drawio](docs/hakathon_architecture.drawio),
который можно импортировать в [draw.io (diagrams.net)](https://www.diagrams.net/ ) для редактирования.

Дополнительные UML диаграммы собраны в отдельном файле [UML.md](docs/plantuml/UML.md)

### 1.1 Архитектура решения

Проект представляет собой многослойное Spring Boot приложение, состоящее из следующих компонентов:

|Компонент|Описание|
|---|---|
|**Frontend**|Реализован на React, предоставляет интерфейс для работы с транзакциями и категориями.|
|**Backend**|Spring Boot сервис на Java 23, реализует REST API, работу с БД и генерацию отчетов (PDF/Excel), использует Spring Security и OAuth2 (Keycloak).|
|**База данных (PostgreSQL)**|Хранение транзакций и категорий. Используется JPA/Hibernate для доступа к данным.|
|**Аутентификация**|Авторизация через Keycloak — OAuth2 Resource Server.|
|**Документация API**|Swagger UI через`springdoc-openapi-starter-webflux-ui`.|
|**Контейнеризация**|Все компоненты запускаются через Docker Compose.|

---
## 2. Структура слоёв backend-приложения

### ✅ Модель данных:

- **Transaction** — основная сущность, описывающая финансовую операцию.
- **Category** — классификатор транзакций.

### 🔁 Слои архитектуры:

- **Controller** : точка входа по HTTP, принимает запросы.
- **Service** : содержит бизнес-логику.
- **Repository** : взаимодействует с базой данных.
- **DTO** : передача данных между клиентом и сервером.
- **Entity** : объекты, маппящиеся на таблицы БД.
- **Mapper** : преобразование между DTO и Entity (через ModelMapper).
- **Exceptions** : централизованная обработка ошибок (`@RestControllerAdvice`).
- **Specification** : фильтрация записей по условиям.

---
## 3. Документирование API с помощью Swagger/OpenAPI

Swagger UI доступен по адресу:

```
http://localhost:8000/swagger-ui/index.html
```

Там вы увидите документацию по всем эндпоинтам:

- `/api/transactions` — CRUD и фильтрация транзакций.
- `/api/categories` — управление категориями.
- `/api/reports` — генерация PDF и Excel отчётов.

## 4. Примеры использования API

Для удобного тестирования API вы можете использовать предоставленную **Postman-коллекцию** .

### 📥 Импорт:

1. Откройте [Postman](https://learning.postman.com/docs/collections/)
2. Нажмите **Import**
3. Выберите файл [Hackathon API.postman_collection.json](docs/Hackathon%20API.postman_collection.json)

### 🧩 Что включает:

- **Transaction Controller** : CRUD операции, фильтрация
- **Category Controller** : управление категориями
- **Report Controller** : генерация PDF и Excel отчётов
- **Авторизация через Keycloak**

### 🔐 Авторизация:

- Для авторизации используется OAuth2:

```http
POST http://localhost:8080/realms/hackathon-realm/protocol/openid-connect/token
Body (x-www-form-urlencoded):
client_id=hackathon-frontend
grant_type=password
username=user1
password=password123
```

### ✅ Создание транзакции:

```http
POST http://localhost:8000/api/transactions
Content-Type: application/json

{
  "personType": "LEGAL",
  "operationDate": "2025-04-05T12:30:00",
  "transactionType": "INCOME",
  "amount": 5000.0,
  "status": "NEW",
  "senderBank": "Alpha Bank",
  "account": "ACC123",
  "receiverBank": "Beta Bank",
  "receiverAccount": "REC456",
  "receiverInn": "1234567890",
  "category": "SALARY",
  "receiverPhone": "+79876543210"
}
```

### 🔍 Поиск транзакций:

```http
GET http://localhost:8000/api/transactions?senderBank=Alpha%20Bank&amountMin=1000&dateFrom=2025-04-01T00:00&dateTo=2025-04-10T23:59
```

### 📄 Генерация отчёта:

```http
GET http://localhost:8000/api/reports/transactions/pdf
Accept: application/pdf
```

или

```http
GET http://localhost:8000/api/reports/transactions/excel
Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
```

## 5. Тестирование

### ✅ Unit-тесты:

Покрывают:

- Валидацию данных (`ValidationServiceImplTest`)
- Преобразование DTO ↔ Entity (`TransactionConverterTest`)
- Работу со спецификациями (`TransactionSpecificationTest`)
- Логику сервисов (`TransactionServiceImplTest`, `CategoryServiceImplTest`)
### ✅ Интеграционные тесты:

- С использованием Testcontainers и PostgreSQL.
- Проверяют работу с базой данных и REST-контроллерами:
    - `TransactionControllerIntegrationTest`
    - `ReportControllerIntegrationTest`
    - `TransactionRepositoryTest`

> Используются случайные порты и интеграция с реальной БД во время тестов.

Отчет о тестирование лежит здесь [TestingReport.md](docs/TestingReport.md)
Дополнительно приложен отчет allure о выполнении unit-тестов [allure-maven](docs/allure-maven)
---
## 6. Документация по развёртыванию

### 📦 Зависимости

- **Java 23**
- **Maven 3.x**
- **Docker / Docker Compose**
- **Node.js 18+** (для фронтенда)

### 🚀 Как запустить локально:

#### 1. Собрать бэкенд:

```bash
cd backend
mvn clean package
```
#### 2. Собрать фронтенд (если нужно):

```bash
cd frontend
npm install
npm run build
```
#### 3. Поднять всё через Docker Compose:

```bash
docker-compose up --build
```
#### 4. Открыть:

- **Frontend** : [http://localhost:3000](http://localhost:3000/)
- **Swagger UI** : [http://localhost:8000/swagger-ui/index.html](http://localhost:8000/swagger-ui/index.html)
- **Keycloak Admin Console** : [http://localhost:8080](http://localhost:8080/)
  Логин: `admin`, пароль: `admin`

---

## 7. Линтеры и форматтеры кода

Для обеспечения чистоты, читаемости и соответствия кода стандартам в проекте внедрены следующие инструменты:

### ✅ Lombok

- **Описание:** Упрощает написание кода за счёт аннотаций (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder` и др.).
- **Преимущество:** Снижает boilerplate-код (геттеры, сеттеры, toString и т.д.).

### ✅ ModelMapper

- **Описание:** Библиотека для автоматического преобразования между DTO и Entity.
- **Преимущество:** Упрощает маппинг объектов, уменьшает количество ручного кода.

### ✅ Checkstyle

- **Описание:** Инструмент статической проверки соответствия Java-кода заданному стилю кодирования.
- **Конфигурация:** Используется файл `google_checks.xml`, который следует [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) .
- **Плагин:** `maven-checkstyle-plugin`
- **Поведение:**
    - Проверка выполняется при сборке (`mvn checkstyle:check`)
    - Если найдены нарушения — сборка завершается ошибкой.
- **Отчет:** HTML-отчет генерируется по пути `target/site/checkstyle.html`.

Пример конфигурации:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-checkstyle-plugin</artifactId>
  <version>3.5.0</version>
  <configuration>
    <configLocation>src/main/resources/google_checks.xml</configLocation>
    <consoleOutput>true</consoleOutput>
    <failsOnError>true</failsOnError>
    <failOnViolation>true</failOnViolation>
  </configuration>
  <executions>
    <execution>
      <goals><goal>check</goal></goals>
    </execution>
  </executions>
</plugin>
```

### ✅ fmt-maven-plugin (Spotify + Google Java Format)

- **Описание:** Форматирует Java-код согласно стилю Google.
- **Плагин:** `fmt-maven-plugin` от Spotify, использует `google-java-format`.
- **Режим работы:**
    - `mvn fmt:check` — проверяет, соответствует ли код стандарту.
    - `mvn fmt:format` — автоматически форматирует код (при добавлении цели в `pom.xml`).

Пример конфигурации:

```xml
<plugin>
  <groupId>com.spotify.fmt</groupId>
  <artifactId>fmt-maven-plugin</artifactId>
  <version>2.24</version>
  <executions>
    <execution>
      <goals>
        <goal>check</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```
