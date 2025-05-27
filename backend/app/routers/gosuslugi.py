from fastapi import APIRouter

router = APIRouter(prefix="/auth", tags=["auth"])

@router.get("/gosuslugi")
async def gosuslugi():
    # always returns token 'demo-token'
    return {"access_token": "demo-token", "token_type": "bearer"}