const express = require('express');
const { check } = require('express-validator');
const authController = require('../controllers/authController');
const auth = require('../middleware/auth');

const router = express.Router();

// @route   POST /api/auth/request-code
// @desc    Request SMS verification code
// @access  Public
router.post(
  '/request-code',
  [
    check('phone', 'Phone number is required').not().isEmpty()
  ],
  authController.requestVerificationCode
);

// @route   POST /api/auth/verify
// @desc    Verify SMS code and login/register
// @access  Public
router.post(
  '/verify',
  [
    check('phone', 'Phone number is required').not().isEmpty(),
    check('code', 'Verification code is required').not().isEmpty().isLength({ min: 6, max: 6 })
  ],
  authController.verifyCodeAndLogin
);

// @route   GET /api/auth/me
// @desc    Get current user
// @access  Private
router.get('/me', auth.protect, authController.getCurrentUser);

module.exports = router;