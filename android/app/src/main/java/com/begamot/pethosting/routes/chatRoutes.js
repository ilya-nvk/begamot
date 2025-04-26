const express = require('express');
const { check } = require('express-validator');
const chatController = require('../controllers/chatController');
const auth = require('../middleware/auth');

const router = express.Router();

// @route   GET /api/chats
// @desc    Get all chats for current user
// @access  Private
router.get('/', auth.protect, chatController.getChats);

// @route   POST /api/chats
// @desc    Create a new chat or get existing
// @access  Private
router.post(
  '/',
  auth.protect,
  [
    check('userId', 'User ID is required').not().isEmpty(),
    check('initialMessage', 'Initial message is required').optional()
  ],
  chatController.createOrGetChat
);

// @route   GET /api/chats/:chatId/messages
// @desc    Get chat messages
// @access  Private
router.get('/:chatId/messages', auth.protect, chatController.getChatMessages);

// @route   POST /api/chats/:chatId/messages
// @desc    Send a message
// @access  Private
router.post(
  '/:chatId/messages',
  auth.protect,
  [
    check('text', 'Message text is required').not().isEmpty()
  ],
  chatController.sendMessage
);

module.exports = router;