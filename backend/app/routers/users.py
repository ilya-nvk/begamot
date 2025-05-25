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
ph = PasswordHasher()

async def update_rating(chosen_profile_id: int, new_rating: float):
    next(user for user in _db if user.id == chosen_profile_id).rating = new_rating


@router.get("/", response_model=List[User])
async def read_users():
    return _db

@router.post("/", response_model=User)
async def create_user(data: User):
    data.id = next(_pk)
    # Hashing password
    data.password = ph.hash(data.password)
    _db.append(data)
    # Creating profile for new user
    await create_profile(data.id)
    # Logging in
    #await login(data)
    return data

@router.put("/avatar", response_model=User)
async def update_user_avatar(user_id: int, new_avatar):
    user = next((u for u in _db if u.id == user_id), None)
    user.avatar = new_avatar
    return user

@router.put("/password", response_model=User)
async def update_user_password(user_id: int, new_password):
    user = next((u for u in _db if u.id == user_id), None)
    user.password = ph.hash(new_password)
    return user

@router.put("/name", response_model=User)
async def update_user_name(user_id: int, new_name):
    user = next((u for u in _db if u.id == user_id), None)
    user.name = new_name
    return user