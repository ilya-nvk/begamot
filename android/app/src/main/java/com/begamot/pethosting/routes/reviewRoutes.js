const express = require('express');
const { check } = require('express-validator');
const reviewController = require('../controllers/reviewController');
const auth = require('../middleware/auth');

const router = express.Router();

// @route   POST /api/reviews
// @desc    Create a new review
// @access  Private
router.post(
  '/',
  auth.protect,
  [
    check('recipientId', 'Recipient ID is required').not().isEmpty(),
    check('rating', 'Rating is required and must be between 1 and 5').isInt({ min: 1, max: 5 }),
    check('text', 'Review text is required').not().isEmpty()
  ],
  reviewController.createReview
);

// @route   GET /api/reviews/user/:userId
// @desc    Get user reviews
// @access  Public
router.get('/user/:userId', reviewController.getUserReviews);

module.exports = router;