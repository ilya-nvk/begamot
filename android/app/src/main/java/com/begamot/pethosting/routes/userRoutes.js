const express = require('express');
const { check } = require('express-validator');
const userController = require('../controllers/userController');
const auth = require('../middleware/auth');
const upload = require('../utils/upload');

const router = express.Router();

// @route   PUT /api/users/profile
// @desc    Update user profile
// @access  Private
router.put(
  '/profile',
  auth.protect,
  upload.single('profilePhoto'),
  [
    check('name', 'Name can only contain letters, spaces, and be at least 2 characters')
      .optional()
      .isLength({ min: 2 })
      .matches(/^[a-zA-Z\s]+$/)
  ],
  userController.updateProfile
);

// @route   GET /api/users/:id
// @desc    Get user profile by ID
// @access  Public
router.get('/:id', userController.getUserProfile);

// @route   GET /api/users
// @desc    Search users
// @access  Public
router.get('/', userController.searchUsers);

module.exports = router;