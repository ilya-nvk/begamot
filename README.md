# Pet Sitting Course — Demo Package

Минимально полный проект для курсача Тани.

## Состав
* **backend/** — FastAPI с /ping, /auth/esia-demo, /listings
* **docker-compose.yml + Dockerfile** — поднимают API на :8000
* **docs/** — краткая дока (stack, схема)
* **android/** — заглушка для клиента

## Запуск

```bash
docker compose up --build
```

Открывай <http://localhost:8000/docs> —

* **GET /ping** → `{"pong":true}`
* **POST /listings** → создать объявление
* **GET /auth/esia-demo** → `"demo-token"`

Сгенерировано 14.05.2025 23:32.
## Quickstart

```bash
# 1. Create and activate virtual env (optional)
python -m venv .venv && source .venv/bin/activate

# 2. Install deps
pip install -r backend/requirements.txt

# 3. Run app
make run
```

Open <http://localhost:8000/docs> to explore the API.

### With Docker

```bash
docker compose up --build
```

## Android‑клиент

В директории `android/` находится минимальное приложение на Kotlin (Jetpack Compose), демонстрирующее работу с API.

- Открыть в Android Studio ➜ Run.
- Backend должен быть запущен локально (`make run`).
- Для эмулятора адрес backend уже указан как `http://10.0.2.2:8000`.

Подробнее см. `android/README_android.md`.
