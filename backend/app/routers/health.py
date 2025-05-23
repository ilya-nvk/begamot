from fastapi import APIRouter

router = APIRouter(tags=["health"])

@router.get("/healthz", summary="Health check")
async def healthz():
    return {"status": "ok"}
