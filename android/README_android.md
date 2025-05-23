# Android Client (Begemot)

## Быстрый старт

1. Откройте папку `android` в **Android Studio Flamingo/Hephaestus** (Plugin & SDK 34).
2. Убедитесь, что backend запущен локально:  
   ```bash
   make run  # в корне проекта
   ```
3. Запустите эмулятор (или подключите устройство) и нажмите **Run** (▶️).

> **Важно**: в `MainActivity.kt` хост сервера указан как `10.0.2.2` — это «localhost» для Android‑эмулятора.  
> Если backend работает на другом IP/порту, поменяйте строки:
> ```kotlin
> client.get("http://IP:PORT/ping")
> client.get("http://IP:PORT/listings")
> ```
