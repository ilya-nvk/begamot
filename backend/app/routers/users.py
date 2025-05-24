from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
import itertools
from argon2 import PasswordHasher

router = APIRouter(prefix="/users", tags=["users"])

class Users(BaseModel):
    id: int
    name: str
    contact: str
    password: str
    avatar: bytes

_db: List[Users] = []
_pk = itertools.count(1)

@router.get("/", response_model=List[Users])
async def read_users():
    return _db

@router.post("/", response_model=Users)
async def create_user(data: Users):
    data.id = next(_pk)
    # ����������� ������
    ph = PasswordHasher()    
    new_data = data    
    new_data.password = ph.hash(data.password)
    _db.append(new_data)    
    return new_data