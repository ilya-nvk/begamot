from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional

class Response(BaseModel):
    listing_id: int
    responder_id: int
    timestamp: datetime = datetime.now()
    comment: Optional[str] = None

_db: List[Response] = []