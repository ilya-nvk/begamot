from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
from datetime import datetime
import base64
from ..models.profile import Profile, _db

router = APIRouter(prefix="/profiles", tags=["profiles"])

@router.get("/", response_model=List[Profile])
async def read_profiles():
    return _db

async def create_profile(user_id: int):
    _db.append(Profile(user_id=user_id, info=""))