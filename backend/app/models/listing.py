from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional

class Listing(BaseModel):
    id: int
    publisher_id: int
    pub_date: datetime
    amount: int
    species: List[str]
    my_place: bool
    start_date: datetime
    end_date: datetime
    money: int
    per: str
    description: Optional[str] = None
    img: Optional[str] = None
    viewers: int = 0
    responses: int = 0

class Filter(BaseModel):
    species: Optional[List[str]] = None
    place: Optional[bool] = None #false - my home, true - pet's home
    start_date: Optional[datetime] = None
    end_date: Optional[datetime] = None
    min_money: Optional[int] = None
    max_money: Optional[int] = None
    per: Optional[str] = None

_db: List[Listing] = []