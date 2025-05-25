from pydantic import BaseModel
from datetime import datetime
from typing import List

class Response(BaseModel):
    listing_id: int
    responder_id: int
    timestamp: datetime = datetime.now()
    comment: str | None = None

_db: List[Response] = []