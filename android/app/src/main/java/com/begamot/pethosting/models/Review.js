const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const ReviewSchema = new Schema({
  author: {
    type: Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  recipient: {
    type: Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  rating: {
    type: Number,
    required: true,
    min: 1,
    max: 5
  },
  text: {
    type: String,
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

// Prevent user from reviewing themselves
ReviewSchema.pre('save', function(next) {
  if (this.author.toString() === this.recipient.toString()) {
    const error = new Error('Users cannot review themselves');
    return next(error);
  }
  next();
});

module.exports = mongoose.model('Review', ReviewSchema);