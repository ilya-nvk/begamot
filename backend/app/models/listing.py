from pydantic import BaseModel
from datetime import datetime
from typing import List

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
    description: str | None = None
    img: str | None = None
    viewers: int = 0
    responses: int = 0

class Filter(BaseModel):
    species: List[str] | None = None
    place: bool | None = None #false - my home, true - pet's home
    start_date: datetime | None = None
    end_date: datetime | None = None
    min_money: int | None = None
    max_money: int | None = None
    per: str | None = None

_db: List[Listing] = []