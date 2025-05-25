from pydantic import BaseModel
from datetime import datetime
from typing import List

class Review(BaseModel):
    profile_id: int
    from_user_id: int
    score: int
    text: str | None = None
    post_date: datetime

_db: List[Review] = []