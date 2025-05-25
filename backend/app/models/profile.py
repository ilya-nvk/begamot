from pydantic import BaseModel
from datetime import datetime
from typing import List

class Profile(BaseModel):
    user_id: int
    info: str | None = None

_db: List[Profile] = []