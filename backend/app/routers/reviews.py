from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
from datetime import datetime
import base64
from .users import update_rating
from ..models.review import Review, _db

router = APIRouter(prefix="/reviews", tags=["reviews"])

async def calc_new_rating(chosen_profile_id: int):
    scores = [item.score for item in _db if item.profile_id == chosen_profile_id]
    new_rating = sum(scores) / len(scores)
    await update_rating(chosen_profile_id, new_rating)


@router.get("/", response_model=List[Review])
async def read_reviews(chosen_profile_id: int):
    return list(filter(lambda item: item.profile_id == chosen_profile_id, _db))

@router.post("/", response_model=Review)
async def create_review(data: Review):
    _db.append(data)
    await calc_new_rating(data.profile_id)
    return data