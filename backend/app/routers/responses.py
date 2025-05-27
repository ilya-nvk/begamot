from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
from datetime import datetime
import itertools
from ..models.response import Response, _db

router = APIRouter(prefix="/responses", tags=["responses"])

@router.get("/", response_model=List[Response])
async def read_responses(listing_id: int):
    return list(filter(lambda item: item.listing_id == listing_id, _db))

@router.post("/", response_model=Response)
async def create_response(data: Response):
    _db.append(data)
    return data