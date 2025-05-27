from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional

class User(BaseModel):
    id: int
    name: str
    contact: str
    password: str
    avatar: Optional[str] = None
    rating: float = 0

_db: List[User] = []