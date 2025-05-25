from pydantic import BaseModel
from datetime import datetime
from typing import List

class Review(BaseModel):
    profile_id: int
    from_user_id: int
    score: int
    text: str | None = None
    timestamp: datetime = datetime.now()

_db: List[Review] = []