# CRUD приложение "Список подарков"
## Требования
- Язык программирования: Java 17
- Сборка приложения: Maven
- СУБД: PostgreSQL
- Логирование: SLF4J, Logback
- Тесты: JUnit5, Mockito
- Документация: JavaDoc
- Фреймворк: Spring

## Для работы с приложением необходимо:
- Настроить подключение к базе данных в application.yaml. Подставить свои данные
    - `jdbc:postgresql://localhost:5432/{Ваша_таблица}`
    - `username: {Ваш логин}`
    - `password: {Ваш пароль}`
- Инициализировать таблицу "Gift" с помощью скрипта `resources/db/init_db.sql`
- Заполнить таблицу "Gift" тестовыми данными с помощью скрипта `resources/db/populate_db.sql`

### Wishlist REST API
**Получение списка всех подарков:**`curl -X GET http://localhost:8080/api/gifts`

**Получение списка подарков, отфильтрованных по важности:**`curl -X GET 'http://localhost:8080/api/gifts?importance={importance}'`
Варианты {importance}: LOW, MEDIUM, HIGH

**Получение подарка по ID:**`curl -X GET http://localhost:8080/api/gifts/{id}`

**Создание нового подарка:**`curl -X POST http://localhost:8080/api/gifts -H 'Content-Type: application/json' -d '{"title": "Новый подарок", "description": "Новое описание", "importance": "HIGH"}'`

**Обновление подарка:**`curl -X PUT http://localhost:8080/api/gifts/1 -H 'Content-Type: application/json' -d '{"id": {id}, "title": "Новое название", "description": "Обновленное название", "importance": "LOW"}'`

**Удаление подарка:**`curl -X DELETE http://localhost:8080/api/gifts/{id}`