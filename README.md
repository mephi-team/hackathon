# hackathon

Запуск системы:
```bash
docker compose up
```

Запуск только БД:
```bash
docker compose up db
```

Проверка работы интерфейса: [ссылка](http://localhost:3000)

Пользователь: admin/admin123

Проверка работы api:
```bash
curl --request GET -sL --url 'http://localhost:8000/api/transactions'
```