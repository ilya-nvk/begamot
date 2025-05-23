from fastapi import APIRouter

router = APIRouter(tags=["misc"])

@router.get("/ping")
async def ping():
    return {"pong": True}