from pydantic import BaseModel
from datetime import datetime
from typing import List
import uuid

class Message(BaseModel):
    message_id: str = uuid.uuid4().hex
    sender_id: str
    recipient_id: str
    content: str
    timestamp: datetime = datetime.now()
    is_read: bool = False
