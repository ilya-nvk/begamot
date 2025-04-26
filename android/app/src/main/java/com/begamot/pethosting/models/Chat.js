const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const ChatSchema = new Schema({
  participants: [{
    type: Schema.Types.ObjectId,
    ref: 'User',
    required: true
  }],
  advertisement: {
    type: Schema.Types.ObjectId,
    ref: 'Advertisement'
  },
  lastMessage: {
    type: Schema.Types.ObjectId,
    ref: 'Message'
  },
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  },
  isActive: {
    type: Boolean,
    default: true
  }
});

// Ensure chat has exactly 2 participants
ChatSchema.pre('save', function(next) {
  if (this.participants.length !== 2) {
    const error = new Error('Chat must have exactly 2 participants');
    return next(error);
  }
  next();
});

// Index for faster chat lookup
ChatSchema.index({ participants: 1 });

module.exports = mongoose.model('Chat', ChatSchema);