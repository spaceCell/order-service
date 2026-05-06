# Order Service

REST-сервис для управления заказами на Spring Boot.

## Технологии

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Cloud OpenFeign
- Spring AMQP (RabbitMQ)
- H2 (in-memory)
- Flyway
- Resilience4j (Retry, RateLimiter, Bulkhead, Circuit Breaker)
- Swagger/OpenAPI (`springdoc`)

## Запуск локально

```bash
./gradlew bootRun
```

Сервис стартует на `http://localhost:8081`.

## Полезные URL

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`
- H2 Console: `http://localhost:8081/h2-console`
  - JDBC URL: `jdbc:h2:mem:orderdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
  - User: `sa`
  - Password: *(пусто)*

## Flyway миграции

Миграции лежат в `src/main/resources/db/migration`.
При старте приложения выполняется `V1__init_schema.sql`.

## API

Базовый путь: `/api/orders`

### Создать заказ

```bash
curl -X POST "http://localhost:8081/api/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 101,
    "status": "NEW",
    "shippingAddress": {
      "street": "Lenina 1",
      "city": "Moscow",
      "postalCode": "101000",
      "country": "Russia"
    },
    "items": [
      {
        "productName": "Phone",
        "quantity": 1,
        "price": {
          "amount": 49999.99,
          "currency": "RUB"
        }
      },
      {
        "productName": "Case",
        "quantity": 2,
        "price": {
          "amount": 999.50,
          "currency": "RUB"
        }
      }
    ]
  }'
```

### Получить все заказы

```bash
curl "http://localhost:8081/api/orders"
```

### Получить заказ по ID

```bash
curl "http://localhost:8081/api/orders/1"
```

### Обновить заказ

```bash
curl -X PUT "http://localhost:8081/api/orders/1" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 101,
    "status": "CONFIRMED",
    "shippingAddress": {
      "street": "Lenina 1",
      "city": "Moscow",
      "postalCode": "101000",
      "country": "Russia"
    },
    "items": [
      {
        "productName": "Phone",
        "quantity": 1,
        "price": {
          "amount": 48999.99,
          "currency": "RUB"
        }
      }
    ]
  }'
```

### Удалить заказ

```bash
curl -X DELETE "http://localhost:8081/api/orders/1"
```

### Создать платеж для заказа (синхронно)

```bash
curl -X POST "http://localhost:8081/api/orders/1/payment" \
  -H "Content-Type: application/json" \
  -d '{
    "method": "CARD"
  }'
```

### Отправить платеж асинхронно через RabbitMQ

```bash
curl -X POST "http://localhost:8081/api/orders/1/payment/async" \
  -H "Content-Type: application/json" \
  -d '{
    "method": "CARD"
  }'
```

## Docker

### Сборка и запуск контейнера

```bash
./gradlew clean bootJar
docker build -t order-service .
docker run --rm -p 8081:8081 order-service
```

### Запуск через docker-compose

```bash
./gradlew clean bootJar
docker compose up --build
```

Поднимаются:

- `order-service` на `8081`
- `rabbitmq` на `5673` (внутри сети Docker: `5672`)
- RabbitMQ UI на `15673`

## Переменные окружения

- `CLIENTS_PAYMENT_SERVICE_URL` (default: `http://localhost:8082`)
- `RABBITMQ_HOST` (default: `localhost`)
- `RABBITMQ_PORT` (default: `5672`)
- `RABBITMQ_USERNAME` (default: `admin`)
- `RABBITMQ_PASSWORD` (default: `admin`)
