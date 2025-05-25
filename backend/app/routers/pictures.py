from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
from datetime import datetime
import base64
from ..models.picture import Picture, _db

router = APIRouter(prefix="/pictures", tags=["pictures"])

@router.get("/", response_model=List[Picture])
async def read_pictures(chosen_profile_id: int):
    return list(filter(lambda item: item.profile_id == chosen_profile_id, _db))

@router.post("/", response_model=Picture)
async def add_picture(img_name: str, profile_id: int):
    data = Picture(profile_id=profile_id,img="")
    with open(img_name, "rb") as f:
        data.img = base64.b64encode(f.read()).decode("utf-8")
    _db.append(data)
    return data