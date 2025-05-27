from fastapi import APIRouter, HTTPException, UploadFile, File
from s3_service import upload_to_s3
from pydantic import BaseModel
from typing import List
from datetime import datetime
import itertools
from ..models.listing import Listing, Filter, _db

router = APIRouter(prefix="/listings", tags=["listings"])

_pk = itertools.count(1)

@router.get("/", response_model=List[Listing])
async def read_listings(flt: Filter):
    if flt is None:
        return _db
    filtered = List[Listing]
    for listing in _db:
        if flt.species is not None:
            for spes in flt.species:
                if spes in listing.species:
                    filtered.append(listing)
        elif flt.place is not None and flt.place == listing.my_place:
            filtered.append(listing)
        elif flt.start_date is not None and flt.start_date >= listing.start_date:
            filtered.append(listing)
        elif flt.end_date is not None and flt.end_date <= listing.end_date:
            filtered.append(listing)
        elif flt.min_money is not None and flt.min_money <= listing.money:
            filtered.append(listing)
        elif flt.max_money is not None and flt.max_money >= listing.money:
            filtered.append(listing)
        elif flt.per is not None and flt.per == listing.per:
            filtered.append(listing)
    return filtered

@router.post("/", response_model=Listing)
async def create_listing(publisher_id: int, pub_date: datetime, amount: int, species: List[str], my_place: bool,
                         start_date: datetime, end_date: datetime, money: int, per: str,
                         description: str, img: str):
    data = Listing(id=next(_pk), publisher_id=publisher_id, pub_date=pub_date, amount=amount,
                   species=species, my_place=my_place, start_date=start_date, end_date=end_date,
                   money=money, per=per, description=description, img=img)
    _db.append(data)
    return data