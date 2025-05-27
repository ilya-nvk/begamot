
import pytest
from httpx import AsyncClient
'''
@pytest.mark.asyncio
async def test_get_users():
    async with AsyncClient(app=app, base_url="http://test") as ac:
        response = await ac.get("/users")

    assert response.status_code == 200

    # Проверяем, что ответ — список
    users_data = response.json()
    assert isinstance(users_data, list)

    # Проверяем, что каждый элемент соответствует модели Users
    for user_data in users_data:
        user = User(**user_data)
        assert isinstance(user.id, int)
        assert isinstance(user.name, str)
        # Дополнительные проверки полей по необходимости


import pytest
from httpx import AsyncClient
from backend.app.main import app
from backend.app.models.user import User

@pytest.mark.asyncio
async def test_get_users():
    async with AsyncClient(app=app, base_url="http://testserver") as ac:
        response = await ac.get("/users")
    assert response.status_code == 200
    assert isinstance(response.json(), list)
'''


@pytest.mark.asyncio
async def test_users(client):
    response = await client.get("/users")
    assert response.status_code == 200
    assert isinstance(response.json(), list)