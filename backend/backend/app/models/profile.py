from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional

class Profile(BaseModel):
    user_id: int
    info: Optional[str] = None

_db: List[Profile] = []