from pydantic import BaseModel
from typing import Optional

class Token(BaseModel):
    access_token: str
    token_type: str
    refresh_token: str

class TokenData(BaseModel):
    id: Optional[int] = None

class RefreshTokenRequest(BaseModel):
    refresh_token: str