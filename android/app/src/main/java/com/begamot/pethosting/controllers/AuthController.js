const jwt = require('jsonwebtoken');
const { validationResult } = require('express-validator');
const User = require('../models/User');
const VerificationCode = require('../models/VerificationCode');
const twilioService = require('../utils/twilioService');

// Generate JWT token
const generateToken = (id) => {
  return jwt.sign({ id }, process.env.JWT_SECRET, {
    expiresIn: process.env.JWT_EXPIRES_IN
  });
};

// Request SMS verification code
exports.requestVerificationCode = async (req, res, next) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    const { phone } = req.body;

    // Format phone number to international format
    let formattedPhone = phone;
    if (!formattedPhone.startsWith('+')) {
      formattedPhone = `+${formattedPhone}`;
    }

    // Generate random 6-digit code
    const verificationCode = Math.floor(100000 + Math.random() * 900000).toString();

    // Save code to database
    await VerificationCode.findOneAndDelete({ phone: formattedPhone });
    await VerificationCode.create({
      phone: formattedPhone,
      code: verificationCode
    });

    // Send SMS with code
    await twilioService.sendSMS(
      formattedPhone,
      `Your verification code is: ${verificationCode}`
    );

    res.status(200).json({
      success: true,
      message: 'Verification code sent successfully'
    });
  } catch (error) {
    console.error('SMS Verification Error:', error);
    next(error);
  }
};

// Verify SMS code and login/register
exports.verifyCodeAndLogin = async (req, res, next) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    const { phone, code } = req.body;

    // Format phone number to international format
    let formattedPhone = phone;
    if (!formattedPhone.startsWith('+')) {
      formattedPhone = `+${formattedPhone}`;
    }

    // Find verification code
    const verificationData = await VerificationCode.findOne({
      phone: formattedPhone,
      code
    });

    if (!verificationData) {
      return res.status(400).json({
        success: false,
        error: 'Invalid or expired verification code'
      });
    }

    // Find existing user or create new one
    let user = await User.findOne({ phone: formattedPhone });
    let isNewUser = false;

    if (!user) {
      // Create new user
      user = await User.create({
        phone: formattedPhone,
        isVerified: true
      });
      isNewUser = true;
    } else {
      // Update existing user
      user.isVerified = true;
      await user.save();
    }

    // Delete used verification code
    await VerificationCode.findByIdAndDelete(verificationData._id);

    // Generate JWT token
    const token = generateToken(user._id);

    res.status(200).json({
      success: true,
      isNewUser,
      token,
      user: {
        id: user._id,
        phone: user.phone,
        name: user.name,
        profilePhoto: user.profilePhoto,
        averageRating: user.averageRating,
        totalRatings: user.totalRatings,
        isVerified: user.isVerified
      }
    });
  } catch (error) {
    next(error);
  }
};

// Get current logged in user
exports.getCurrentUser = async (req, res, next) => {
  try {
    const user = await User.findById(req.user.id);

    res.status(200).json({
      success: true,
      user: {
        id: user._id,
        phone: user.phone,
        name: user.name,
        profilePhoto: user.profilePhoto,
        averageRating: user.averageRating,
        totalRatings: user.totalRatings,
        isVerified: user.isVerified,
        createdAt: user.createdAt
      }
    });
  } catch (error) {
    next(error);
  }
};