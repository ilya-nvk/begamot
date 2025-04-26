const { validationResult } = require('express-validator');
const mongoose = require('mongoose');
const Chat = require('../models/Chat');
const Message = require('../models/Message');
const User = require('../models/User');

// Get all chats for current user
exports.getChats = async (req, res, next) => {
  try {
    const chats = await Chat.find({
      participants: { $in: [req.user.id] },
      isActive: true
    })
      .populate('participants', 'name profilePhoto')
      .populate('advertisement', 'title photos')
      .populate('lastMessage')
      .sort({ updatedAt: -1 });

    res.status(200).json({
      success: true,
      count: chats.length,
      chats: chats.map(chat => {
        // Filter out current user from participants
        const otherParticipants = chat.participants.filter(
          p => p._id.toString() !== req.user.id
        );

        return {
          id: chat._id,
          participant: otherParticipants[0], // The other user
          advertisement: chat.advertisement,
          lastMessage: chat.lastMessage,
          createdAt: chat.createdAt,
          updatedAt: chat.updatedAt
        };
      })
    });
  } catch (error) {
    next(error);
  }
};

// Create new chat or get existing chat
exports.createOrGetChat = async (req, res, next) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    const { userId, advertisementId, initialMessage } = req.body;

    // Prevent chat with self
    if (userId === req.user.id) {
      return res.status(400).json({
        success: false,
        error: 'Cannot create chat with yourself'
      });
    }

    // Check if user exists
    const otherUser = await User.findById(userId);
    if (!otherUser) {
      return res.status(404).json({
        success: false,
        error: 'User not found'
      });
    }

    // Check for existing chat between these users
    let chat = await Chat.findOne({
      participants: { $all: [req.user.id, userId] },
      ...(advertisementId ? { advertisement: advertisementId } : {})
    });

    // If no chat exists, create one
    if (!chat) {
      chat = await Chat.create({
        participants: [req.user.id, userId],
        advertisement: advertisementId || null
      });
    }

    // If initial message provided, create it
    if (initialMessage) {
      const message = await Message.create({
        chat: chat._id,
        sender: req.user.id,
        text: initialMessage
      });

      // Update chat with last message
      chat.lastMessage = message._id;
      chat.updatedAt = Date.now();
      await chat.save();
    }

    // Populate chat details
    await chat.populate('participants', 'name profilePhoto');
    await chat.populate('advertisement', 'title photos');
    if (chat.lastMessage) {
      await chat.populate('lastMessage');
    }

    // Filter out current user from participants
    const otherParticipants = chat.participants.filter(
      p => p._id.toString() !== req.user.id
    );

    res.status(chat.isNew ? 201 : 200).json({
      success: true,
      chat: {
        id: chat._id,
        participant: otherParticipants[0], // The other user
        advertisement: chat.advertisement,
        lastMessage: chat.lastMessage,
        createdAt: chat.createdAt,
        updatedAt: chat.updatedAt
      }
    });
  } catch (error) {
    next(error);
  }
};

// Get chat messages
exports.getChatMessages = async (req, res, next) => {
  try {
    const chatId = req.params.chatId;

    // Check if chat exists and user is participant
    const chat = await Chat.findOne({
      _id: chatId,
      participants: { $in: [req.user.id] }
    });

    if (!chat) {
      return res.status(404).json({
        success: false,
        error: 'Chat not found or you are not a participant'
      });
    }

    // Pagination
    const page = parseInt(req.query.page, 10) || 1;
    const limit = parseInt(req.query.limit, 10) || 20;
    const startIndex = (page - 1) * limit;

    // Get messages
    const messages = await Message.find({ chat: chatId })
      .sort({ createdAt: -1 }) // Newest first
      .skip(startIndex)
      .limit(limit)
      .populate('sender', 'name profilePhoto');

    // Update unread messages to read
    await Message.updateMany(
      {
        chat: chatId,
        sender: { $ne: req.user.id },
        isRead: false
      },
      { isRead: true }
    );

    // Get total count for pagination
    const total = await Message.countDocuments({ chat: chatId });

    res.status(200).json({
      success: true,
      count: messages.length,
      total,
      totalPages: Math.ceil(total / limit),
      currentPage: page,
      messages: messages.reverse() // Return in chronological order
    });
  } catch (error) {
    // Check if error is due to invalid ObjectId
    if (error instanceof mongoose.Error.CastError) {
      return res.status(404).json({
        success: false,
        error: 'Chat not found'
      });
    }
    next(error);
  }
};

// Send message
exports.sendMessage = async (req, res, next) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    const { text } = req.body;
    const chatId = req.params.chatId;

    // Check if chat exists and user is participant
    const chat = await Chat.findOne({
      _id: chatId,
      participants: { $in: [req.user.id] }
    });

    if (!chat) {
      return res.status(404).json({
        success: false,
        error: 'Chat not found or you are not a participant'
      });
    }

    // Create message
    const message = await Message.create({
      chat: chatId,
      sender: req.user.id,
      text
    });

    // Update chat with last message
    chat.lastMessage = message._id;
    chat.updatedAt = Date.now();
    await chat.save();

    // Populate sender details
    await message.populate('sender', 'name profilePhoto');

    res.status(201).json({
      success: true,
      message
    });
  } catch (error) {
    next(error);
  }
};