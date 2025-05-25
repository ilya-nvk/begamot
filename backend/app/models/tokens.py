from pydantic import BaseModel
from datetime import datetime
from typing import List

class Token(BaseModel):
    access_token: str
    token_type: str
    refresh_token: str

class TokenData(BaseModel):
    id: int | None = None

class RefreshTokenRequest(BaseModel):
    refresh_token: str