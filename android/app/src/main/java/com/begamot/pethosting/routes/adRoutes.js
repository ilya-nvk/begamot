const express = require('express');
const { check } = require('express-validator');
const adController = require('../controllers/adController');
const auth = require('../middleware/auth');
const upload = require('../utils/upload');

const router = express.Router();

// @route   POST /api/ads
// @desc    Create a new advertisement
// @access  Private
router.post(
  '/',
  auth.protect,
  upload.array('photos', 5), // Max 5 photos
  [
    check('title', 'Title is required').not().isEmpty().trim(),
    check('description', 'Description is required').not().isEmpty(),
    check('price', 'Price is required and must be a number').isNumeric(),
    check('category', 'Category is required').not().isEmpty(),
    check('location', 'Location is required').not().isEmpty()
  ],
  adController.createAd
);

// @route   GET /api/ads
// @desc    Get all advertisements with filtering and pagination
// @access  Public
router.get('/', adController.getAds);

// @route   GET /api/ads/:id
// @desc    Get advertisement by ID
// @access  Public
router.get('/:id', adController.getAdById);

// @route   PUT /api/ads/:id
// @desc    Update advertisement
// @access  Private
router.put(
  '/:id',
  auth.protect,
  upload.array('photos', 5),
  [
    check('title', 'Title must not be empty if provided').optional().not().isEmpty().trim(),
    check('description', 'Description must not be empty if provided').optional().not().isEmpty(),
    check('price', 'Price must be a number if provided').optional().isNumeric(),
    check('category', 'Category must not be empty if provided').optional().not().isEmpty(),
    check('location', 'Location must not be empty if provided').optional().not().isEmpty(),
    check('isActive', 'isActive must be a boolean').optional().isBoolean()
  ],
  adController.updateAd
);

// @route   DELETE /api/ads/:id
// @desc    Delete advertisement
// @access  Private
router.delete('/:id', auth.protect, adController.deleteAd);

// @route   GET /api/ads/user/:userId
// @desc    Get user advertisements
// @access  Public
router.get('/user/:userId', adController.getUserAds);

// @route   GET /api/ads/my
// @desc    Get current user advertisements
// @access  Private
router.get('/my/ads', auth.protect, adController.getUserAds);

module.exports = router;