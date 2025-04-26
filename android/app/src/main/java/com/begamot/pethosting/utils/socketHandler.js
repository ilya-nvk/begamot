const jwt = require('jsonwebtoken');
const User = require('../models/User');
const Message = require('../models/Message');
const Chat = require('../models/Chat');

module.exports = (io) => {
  // Store online users
  const onlineUsers = new Map();
  
  io.use(async (socket, next) => {
    try {
      // Get token from query params
      const token = socket.handshake.auth.token || socket.handshake.query.token;
      
      if (!token) {
        return next(new Error('Authentication error. Token required.'));
      }
      
      // Verify token
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      
      // Get user from token
      const user = await User.findById(decoded.id);
      
      if (!user) {
        return next(new Error('Authentication error. User not found.'));
      }
      
      // Attach user to socket
      socket.user = {
        id: user._id,
        name: user.name,
        profilePhoto: user.profilePhoto
      };
      
      next();
    } catch (error) {
      console.error('Socket authentication error:', error);
      next(new Error('Authentication error. Invalid token.'));
    }
  });
  
  io.on('connection', (socket) => {
    console.log(`User connected: ${socket.user.id}`);
    
    // Add user to online users
    onlineUsers.set(socket.user.id.toString(), socket.id);
    
    // Join room for user-specific events
    socket.join(`user:${socket.user.id}`);
    
    // Join rooms for all user's chats
    Chat.find({ participants: socket.user.id })
      .then(chats => {
        chats.forEach(chat => {
          socket.join(`chat:${chat._id}`);
        });
      })
      .catch(err => console.error('Error joining chat rooms:', err));
    
    // Handle join chat
    socket.on('join-chat', (chatId) => {
      socket.join(`chat:${chatId}`);
    });
    
    // Handle leave chat
    socket.on('leave-chat', (chatId) => {
      socket.leave(`chat:${chatId}`);
    });
    
    // Handle new message
    socket.on('send-message', async (data) => {
      try {
        const { chatId, text } = data;
        
        // Save message to database
        const message = await Message.create({
          chat: chatId,
          sender: socket.user.id,
          text
        });
        
        // Update chat's last message
        await Chat.findByIdAndUpdate(chatId, {
          lastMessage: message._id,
          updatedAt: Date.now()
        });
        
        // Populate sender info
        await message.populate('sender', 'name profilePhoto');
        
        // Broadcast to chat room
        io.to(`chat:${chatId}`).emit('new-message', message);
        
        // Get chat to find other participant
        const chat = await Chat.findById(chatId);
        
        // Send notification to other participant if not in chat room
        chat.participants.forEach(participantId => {
          if (participantId.toString() !== socket.user.id.toString()) {
            // Get socket ID of other participant if online
            const receiverSocketId = onlineUsers.get(participantId.toString());
            
            if (receiverSocketId) {
              io.to(receiverSocketId).emit('new-chat-notification', {
                chatId,
                message
              });
            }
          }
        });
      } catch (error) {
        console.error('Error handling message:', error);
        socket.emit('error', { message: 'Error sending message' });
      }
    });
    
    // Handle typing status
    socket.on('typing', (chatId) => {
      socket.to(`chat:${chatId}`).emit('user-typing', {
        chatId,
        user: socket.user
      });
    });
    
    // Handle stop typing
    socket.on('stop-typing', (chatId) => {
      socket.to(`chat:${chatId}`).emit('user-stop-typing', {
        chatId,
        user: socket.user
      });
    });
    
    // Handle read messages
    socket.on('read-messages', async (chatId) => {
      try {
        // Update all unread messages from other users to read
        await Message.updateMany(
          {
            chat: chatId,
            sender: { $ne: socket.user.id },
            isRead: false
          },
          { isRead: true }
        );
        
        // Emit event to chat room
        socket.to(`chat:${chatId}`).emit('messages-read', {
          chatId,
          userId: socket.user.id
        });
      } catch (error) {
        console.error('Error marking messages as read:', error);
      }
    });
    
    // Handle disconnect
    socket.on('disconnect', () => {
      console.log(`User disconnected: ${socket.user.id}`);
      onlineUsers.delete(socket.user.id.toString());
    });
  });
};