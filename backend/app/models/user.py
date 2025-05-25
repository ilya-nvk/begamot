from pydantic import BaseModel
from datetime import datetime
from typing import List

class User(BaseModel):
    id: int
    name: str
    contact: str
    password: str
    avatar: str | None = None
    rating: float
    def __init__(self):
        self.rating = 0

_db: List[User] = []