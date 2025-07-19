Есть 2 режима аудита (mode: kafka/console)
Kafka:
1. Запуск Kafka:
docker-compose up -d
2. Создание топика:
docker exec -it bishop-kafka-1 kafka-topics \
  --create \
  --topic android-audit-log \
  --bootstrap-server localhost:19092 \
  --partitions 1 \
  --replication-factor 1
3. Запуск приложения:
mvn spring-boot:run
4. Проверка статуса:
curl http://localhost:8081/prototype/status
Вывод: Operational status: NOMINAL
5. Отправка команды:
curl -X POST http://localhost:8081/api/commands \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Проверка запасов воды",
    "priority": "CRITICAL",
    "author": "Анна",
    "time": "2025-07-20T08:00:00Z"
  }'
Ответ: Command accepted
6. Просмотр аудит-логов:
docker exec -it bishop-kafka-1 kafka-console-consumer \
  --topic android-audit-log \
  --bootstrap-server localhost:19092 \
  --from-beginning
Вывод: Method: DemoController.checkStatus() | Params: [] | Result: Operational status: NOMINAL
7. Просмотр метрик:
Размер очереди команд:
http://localhost:8081/actuator/metrics/android.queue.size
Количество выполненных команд по авторам:
http://localhost:actuator/metrics/android.commands.executed
Console:
1. Запуск приложения:
mvn spring-boot:run -Dspring-boot.run.arguments=--audit.mode=console
2. Проверка статуса:
curl http://localhost:8081/prototype/status
В логах:
[AUDIT] Method: DemoController.checkStatus() | Params: [] | Result: Operational status: NOMINAL
Размер очереди команд:
curl http://localhost:8081/actuator/metrics/android.queue.size
Количество выполненных команд по авторам:
curl http://localhost:8081/actuator/metrics/android.commands.executed
Невалидный запрос:
curl -X POST http://localhost:8081/api/commands \
  -H "Content-Type: application/json" \
  -d '{
    "priority": "INVALID",
    "author": "Тест",
    "time": "2025-07-20"
  }'












