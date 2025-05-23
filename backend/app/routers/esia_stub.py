from fastapi import APIRouter

router = APIRouter(prefix="/auth", tags=["auth"])

@router.get("/esia-demo")
async def esia_demo():
    # always returns token 'demo-token'
    return {"access_token": "demo-token", "token_type": "bearer"}