from pydantic import BaseModel
from datetime import datetime
from typing import List

class Picture(BaseModel):
    profile_id: int
    img: str

_db: List[Picture] = []