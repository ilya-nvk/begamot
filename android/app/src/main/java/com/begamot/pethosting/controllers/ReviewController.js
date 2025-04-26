const { validationResult } = require('express-validator');
const Review = require('../models/Review');
const User = require('../models/User');

// Create new review
exports.createReview = async (req, res, next) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    const { recipientId, rating, text } = req.body;

    // Prevent reviewing self
    if (recipientId === req.user.id) {
      return res.status(400).json({
        success: false,
        error: 'You cannot review yourself'
      });
    }

    // Check if recipient exists
    const recipient = await User.findById(recipientId);
    if (!recipient) {
      return res.status(404).json({
        success: false,
        error: 'User not found'
      });
    }

    // Check if user already reviewed this recipient
    const existingReview = await Review.findOne({
      author: req.user.id,
      recipient: recipientId
    });

    if (existingReview) {
      return res.status(400).json({
        success: false,
        error: 'You have already reviewed this user'
      });
    }

    // Create review
    const review = await Review.create({
      author: req.user.id,
      recipient: recipientId,
      rating,
      text
    });

    // Update recipient's rating
    recipient.rating = recipient.rating + rating;
    recipient.totalRatings = recipient.totalRatings + 1;
    await recipient.save();

    // Populate author details
    await review.populate('author', 'name profilePhoto');

    res.status(201).json({
      success: true,
      review
    });
  } catch (error) {
    next(error);
  }
};

// Get user reviews
exports.getUserReviews = async (req, res, next) => {
  try {
    const userId = req.params.userId;

    // Check if user exists
    const user = await User.findById(userId);
    if (!user) {
      return res.status(404).json({
        success: false,
        error: 'User not found'
      });
    }

    // Pagination
    const page = parseInt(req.query.page, 10) || 1;
    const limit = parseInt(req.query.limit, 10) || 10;
    const startIndex = (page - 1) * limit;

    // Get reviews
    const reviews = await Review.find({ recipient: userId })
      .populate('author', 'name profilePhoto')
      .sort({ createdAt: -1 })
      .skip(startIndex)
      .limit(limit);

    // Get total count for pagination
    const total = await Review.countDocuments({ recipient: userId });

    res.status(200).json({
      success: true,
      count: reviews.length,
      total,
      totalPages: Math.ceil(total / limit),
      currentPage: page,
      reviews
    });
  } catch (error) {
    next(error);
  }
};