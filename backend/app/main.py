import logging
from time import perf_counter

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from starlette.requests import Request
from starlette.responses import Response

from .routers import ping, esia_stub, listings, health, users

logging.basicConfig(
    format="%(asctime)s %(levelname)s %(message)s",
    level=logging.INFO,
)

app = FastAPI(title="Begemot", version="1.0.0")

# CORS (wide‑open for demo)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Middleware for timing + basic access log
@app.middleware("http")
async def add_process_time_header(request: Request, call_next):
    start = perf_counter()
    response: Response = await call_next(request)
    duration = perf_counter() - start
    logging.info(
        "%s %s -> %s %.2f ms",
        request.method,
        request.url.path,
        response.status_code,
        duration * 1_000,
    )
    response.headers["X-Process-Time"] = str(round(duration, 3))
    return response

# Routers
app.include_router(ping.router)
app.include_router(esia_stub.router)
app.include_router(listings.router)
app.include_router(users.router)
app.include_router(health.router)