const { validationResult } = require('express-validator');
const User = require('../models/User');
const Review = require('../models/Review');
const fileUpload = require('../utils/fileUpload');

// Update user profile
exports.updateProfile = async (req, res, next) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    const { name } = req.body;
    const updateData = { name };

    // Handle profile photo upload if present
    if (req.file) {
      const fileName = await fileUpload.saveProfilePhoto(req.file);
      updateData.profilePhoto = fileName;
    }

    const user = await User.findByIdAndUpdate(
      req.user.id,
      updateData,
      { new: true, runValidators: true }
    );

    if (!user) {
      return res.status(404).json({
        success: false,
        error: 'User not found'
      });
    }

    res.status(200).json({
      success: true,
      user: {
        id: user._id,
        phone: user.phone,
        name: user.name,
        profilePhoto: user.profilePhoto,
        averageRating: user.averageRating,
        totalRatings: user.totalRatings
      }
    });
  } catch (error) {
    next(error);
  }
};

// Get user profile by ID
exports.getUserProfile = async (req, res, next) => {
  try {
    const user = await User.findById(req.params.id);

    if (!user) {
      return res.status(404).json({
        success: false,
        error: 'User not found'
      });
    }

    // Get user reviews
    const reviews = await Review.find({ recipient: user._id })
      .populate('author', 'name profilePhoto')
      .sort('-createdAt')
      .limit(10);

    res.status(200).json({
      success: true,
      user: {
        id: user._id,
        phone: user.phone,
        name: user.name,
        profilePhoto: user.profilePhoto,
        averageRating: user.averageRating,
        totalRatings: user.totalRatings,
        createdAt: user.createdAt
      },
      reviews
    });
  } catch (error) {
    next(error);
  }
};

// Search users
exports.searchUsers = async (req, res, next) => {
  try {
    const { query } = req.query;
    let searchQuery = {};

    if (query) {
      searchQuery = {
        $or: [
          { name: { $regex: query, $options: 'i' } },
          { phone: { $regex: query, $options: 'i' } }
        ]
      };
    }

    const users = await User.find(searchQuery)
      .select('_id name phone profilePhoto averageRating totalRatings')
      .limit(20);

    res.status(200).json({
      success: true,
      count: users.length,
      users
    });
  } catch (error) {
    next(error);
  }
};