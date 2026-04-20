# order-service

Spring Boot сервис управления заказами с синхронной и асинхронной интеграцией с payment-service.

## Что было исправлено

- добавлены недостающие зависимости для:
  - OpenFeign
  - RabbitMQ (AMQP)
  - Resilience4j (retry/circuit breaker/rate limiter/bulkhead)
  - Swagger annotations
- добавлена рабочая конфигурация `application.properties` для локального старта
- добавлены `Dockerfile` и `docker-compose.yml`
- обновлен smoke-тест контекста приложения (`@SpringBootTest`)

## Требования

- Java 21
- Docker и Docker Compose (для контейнерного запуска)

## Локальный запуск

```bash
./gradlew bootRun
```

Сервис будет доступен на:

- `http://localhost:8081`

H2 console:

- `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:orders;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- user: `sa`
- password: пустой

## Запуск через Docker Compose

```bash
docker compose up --build
```

Поднимаются:

- `order-service` на `8081`
- `rabbitmq` на `5672`
- RabbitMQ UI на `15672` (`guest/guest`)

По умолчанию `order-service` ожидает `payment-service` по адресу:

- `http://host.docker.internal:8082`

Изменить можно переменной окружения `CLIENTS_PAYMENT_SERVICE_URL`.

## Полезные переменные окружения

- `CLIENTS_PAYMENT_SERVICE_URL` (default: `http://localhost:8082`)
- `RABBITMQ_HOST` (default: `localhost`)
- `RABBITMQ_PORT` (default: `5672`)
- `RABBITMQ_USERNAME` (default: `guest`)
- `RABBITMQ_PASSWORD` (default: `guest`)
