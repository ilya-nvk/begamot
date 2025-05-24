from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
from datetime import datetime
import itertools

router = APIRouter(prefix="/listings", tags=["listings"])

class Listing(BaseModel):
    id: int
    publisher_id: int
    pub_date: datetime
    amount: int
    species: str
    my_place: bool
    start_date: datetime
    end_date: datetime
    money: int
    per: str
    description: str
    img: bytes

_db: List[Listing] = []
_pk = itertools.count(1)

@router.get("/", response_model=List[Listing])
async def read_listings():
    return _db

@router.post("/", response_model=Listing)
async def create_listing(data: Listing):
    data.id = next(_pk)
    _db.append(data)
    return data