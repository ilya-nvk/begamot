from fastapi import APIRouter, WebSocket, WebSocketDisconnect
from pydantic import BaseModel
from typing import Dict, List, Optional
from datetime import datetime
import json
import uuid
from ..models.message import Message

router = APIRouter(prefix="/chat", tags=["chat"])

class ConnectionManager:
    def __init__(self):
        self.active_connections: Dict[str, WebSocket] = {}
        self.message_history: List[Message] = []

    async def connect_user(self, user_id: str, websocket: WebSocket):
        await websocket.accept()
        self.active_connections[user_id] = websocket
        await self._notify_online_status(user_id, is_online=True)

    def disconnect_user(self, user_id: str):
        if user_id in self.active_connections:
            del self.active_connections[user_id]
            self._notify_online_status(user_id, is_online=False)

    async def send_message(self, sender_id: str, recipient_id: str, content: str):
        message = Message(
            sender_id=sender_id,
            recipient_id=recipient_id,
            content=content
        )
        self.message_history.append(message)
        if recipient_id in self.active_connections:
            await self._send_ws_message(
                self.active_connections[recipient_id],
                {
                    "type": "new_message",
                    "message": message.dict()
                }
            )
        if sender_id in self.active_connections:
            await self._send_ws_message(
                self.active_connections[sender_id],
                {
                    "type": "message_sent",
                    "message": message.dict()
                }
            )

    async def _notify_online_status(self, user_id: str, is_online: bool):
        # Notify user's contacts about status change
        contacts = self._get_user_contacts(user_id)
        for contact_id in contacts:
            if contact_id in self.active_connections:
                await self._send_ws_message(
                    self.active_connections[contact_id],
                    {
                        "type": "status_update",
                        "user_id": user_id,
                        "is_online": is_online
                    }
                )

    def _get_user_contacts(self, user_id: str) -> List[str]:
        # Get all user's contacts
        contacts = set()
        for msg in self.message_history:
            if msg.sender_id == user_id:
                contacts.add(msg.recipient_id)
            elif msg.recipient_id == user_id:
                contacts.add(msg.sender_id)
        return list(contacts)

    async def _send_ws_message(self, websocket: WebSocket, data: Dict):
        await websocket.send_text(json.dumps(data))

manager = ConnectionManager()

@router.websocket("/ws/{user_id}")
async def private_chat_endpoint(websocket: WebSocket, user_id: str):
    await manager.connect_user(user_id, websocket)
    try:
        while True:
            data = await websocket.receive_json()

            if data["type"] == "send_message":
                await manager.send_private_message(
                    sender_id=user_id,
                    recipient_id=data["recipient_id"],
                    content=data["content"]
                )

    except WebSocketDisconnect:
        manager.disconnect_user(user_id)

@router.get("/messages/{user_id}/{contact_id}", response_model=List[Message])
async def get_chat_history(user_id: str, contact_id: str, limit: int = 4000):
    return [
               msg for msg in manager.message_history
               if (msg.sender_id == user_id and msg.recipient_id == contact_id)
                  or (msg.sender_id == contact_id and msg.recipient_id == user_id)
           ][-limit:]

@router.get("/contacts/{user_id}")
async def get_user_contacts(user_id: str):
    contacts = manager._get_user_contacts(user_id)
    return {
        "contacts": contacts,
        "online_users": [
            contact_id for contact_id in contacts
            if contact_id in manager.active_connections
        ]
    }

@router.post("/messages/{message_id}/read")
async def mark_as_read(message_id: str):
    next(msg for msg in manager.message_history if msg.message_id == message_id).is_read = True
    return {"status": "ok"}