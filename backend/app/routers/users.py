from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
import itertools
from argon2 import PasswordHasher
import base64
from datetime import datetime
from .profiles import create_profile
from .auth import login
from ..models.user import User, _db

router = APIRouter(prefix="/users", tags=["users"])

_pk = itertools.count(1)

async def update_rating(chosen_profile_id: int, new_rating: float):
    next(user for user in _db if user.id == chosen_profile_id).rating = new_rating


@router.get("/", response_model=List[User])
async def read_users():
    return _db

@router.post("/", response_model=User)
async def create_user(data: User):
    data.id = next(_pk)
    # Hashing password
    ph = PasswordHasher()
    data.password = ph.hash(data.password)
    _db.append(data)
    # Creating profile for new user
    await create_profile(data.id)
    # Logging in
    await login(data)
    return data