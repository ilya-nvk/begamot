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

@router.put("/", response_model=Profile)
async def update_profile_info(profile_id: int, new_info: str):
    profile = next((p for p in _db if p.id == profile_id), None)
    profile.info = new_info
    return profile

async def create_profile(user_id: int):
    _db.append(Profile(user_id=user_id, info=""))