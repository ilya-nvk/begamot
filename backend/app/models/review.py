from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional

class Review(BaseModel):
    profile_id: int
    from_user_id: int
    score: int
    text: Optional[str] = None
    timestamp: datetime = datetime.now()

_db: List[Review] = []