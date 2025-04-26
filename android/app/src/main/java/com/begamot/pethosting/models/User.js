const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const UserSchema = new Schema({
  phone: {
    type: String,
    required: true,
    unique: true,
    trim: true
  },
  name: {
    type: String,
    trim: true
  },
  profilePhoto: {
    type: String,
    default: 'default-profile.jpg'
  },
  rating: {
    type: Number,
    default: 0,
    min: 0,
    max: 5
  },
  totalRatings: {
    type: Number,
    default: 0
  },
  createdAt: {
    type: Date,
    default: Date.now
  },
  lastActive: {
    type: Date,
    default: Date.now
  },
  isVerified: {
    type: Boolean,
    default: false
  }
});

// Virtual to calculate average rating
UserSchema.virtual('averageRating').get(function() {
  return this.totalRatings > 0 ? this.rating / this.totalRatings : 0;
});

module.exports = mongoose.model('User', UserSchema);