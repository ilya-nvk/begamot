const { validationResult } = require('express-validator');
const mongoose = require('mongoose');
const Advertisement = require('../models/Advertisement');
const fileUpload = require('../utils/fileUpload');

// Create new advertisement
exports.createAd = async (req, res, next) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    const { title, description, price, category, location } = req.body;

    // Create ad without photos first
    const newAd = await Advertisement.create({
      title,
      description,
      price,
      category,
      location,
      user: req.user.id
    });

    // Handle photo uploads if present
    if (req.files && req.files.length > 0) {
      const photoFileNames = await fileUpload.saveAdPhotos(req.files);

      // Update ad with photo filenames
      newAd.photos = photoFileNames;
      await newAd.save();
    }

    res.status(201).json({
      success: true,
      advertisement: newAd
    });
  } catch (error) {
    next(error);
  }
};

// Get all advertisements with pagination
exports.getAds = async (req, res, next) => {
  try {
    // Pagination
    const page = parseInt(req.query.page, 10) || 1;
    const limit = parseInt(req.query.limit, 10) || 10;
    const startIndex = (page - 1) * limit;

    // Filtering
    let query = { isActive: true };

    if (req.query.category) {
      query.category = req.query.category;
    }

    if (req.query.minPrice && req.query.maxPrice) {
      query.price = {
        $gte: parseInt(req.query.minPrice),
        $lte: parseInt(req.query.maxPrice)
      };
    } else if (req.query.minPrice) {
      query.price = { $gte: parseInt(req.query.minPrice) };
    } else if (req.query.maxPrice) {
      query.price = { $lte: parseInt(req.query.maxPrice) };
    }

    if (req.query.search) {
      query.$or = [
        { title: { $regex: req.query.search, $options: 'i' } },
        { description: { $regex: req.query.search, $options: 'i' } }
      ];
    }

    // Execute query with pagination
    const ads = await Advertisement.find(query)
      .populate('user', 'name profilePhoto averageRating')
      .sort({ createdAt: -1 })
      .skip(startIndex)
      .limit(limit);

    // Get total count for pagination
    const total = await Advertisement.countDocuments(query);

    res.status(200).json({
      success: true,
      count: ads.length,
      total,
      totalPages: Math.ceil(total / limit),
      currentPage: page,
      advertisements: ads
    });
  } catch (error) {
    next(error);
  }
};

// Get advertisement by ID
exports.getAdById = async (req, res, next) => {
  try {
    const ad = await Advertisement.findById(req.params.id)
      .populate('user', 'name phone profilePhoto averageRating totalRatings');

    if (!ad) {
      return res.status(404).json({
        success: false,
        error: 'Advertisement not found'
      });
    }

    // Increment view count
    ad.views += 1;
    await ad.save();

    res.status(200).json({
      success: true,
      advertisement: ad
    });
  } catch (error) {
    // Check if error is due to invalid ObjectId
    if (error instanceof mongoose.Error.CastError) {
      return res.status(404).json({
        success: false,
        error: 'Advertisement not found'
      });
    }
    next(error);
  }
};

// Update advertisement
exports.updateAd = async (req, res, next) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    const { title, description, price, category, location, isActive } = req.body;

    // Find advertisement
    let ad = await Advertisement.findById(req.params.id);

    if (!ad) {
      return res.status(404).json({
        success: false,
        error: 'Advertisement not found'
      });
    }

    // Check ownership
    if (ad.user.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        error: 'Not authorized to update this advertisement'
      });
    }

    // Update fields
    const updateData = {
      title: title || ad.title,
      description: description || ad.description,
      price: price || ad.price,
      category: category || ad.category,
      location: location || ad.location,
      isActive: isActive !== undefined ? isActive : ad.isActive,
      updatedAt: Date.now()
    };

    // Handle new photos if any
    if (req.files && req.files.length > 0) {
      const photoFileNames = await fileUpload.saveAdPhotos(req.files);
      updateData.photos = [...ad.photos, ...photoFileNames];
    }

    // Update ad
    ad = await Advertisement.findByIdAndUpdate(
      req.params.id,
      updateData,
      { new: true, runValidators: true }
    ).populate('user', 'name profilePhoto averageRating');

    res.status(200).json({
      success: true,
      advertisement: ad
    });
  } catch (error) {
    next(error);
  }
};