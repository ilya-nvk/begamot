'''
import sys
import pathlib

ROOT = pathlib.Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))
'''
import pytest
from fastapi import FastAPI
from httpx import AsyncClient
import sys
from pathlib import Path

sys.path.append(str(Path(__file__).parent.parent))

@pytest.fixture
def app() -> FastAPI:
    from backend.app.main import app
    return app

@pytest.fixture
async def client(app: FastAPI) -> AsyncClient:
    async with AsyncClient(app=app, base_url="http://test") as client:
        yield client