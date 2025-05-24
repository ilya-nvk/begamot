FROM python:3.12-slim AS base

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

COPY backend/requirements.txt .

# Use pip cache to speed up builds
RUN --mount=type=cache,target=/root/.cache/pip pip install --no-cache-dir -r requirements.txt
RUN pip install argon2-cffi

COPY . .

CMD ["uvicorn", "backend.app.main:app", "--host", "0.0.0.0", "--port", "8000"]
