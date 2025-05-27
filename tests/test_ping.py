import pytest
from httpx import AsyncClient
'''
#@pytest.mark.asyncio
async def test_ping():
    async with AsyncClient(app=app, base_url="http://test") as ac:
        resp = await ac.get("/ping")
    assert resp.status_code == 200
    assert "pong" in resp.text.lower()
'''
@pytest.mark.asyncio
async def test_ping(client):
    response = await client.get("/ping")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}