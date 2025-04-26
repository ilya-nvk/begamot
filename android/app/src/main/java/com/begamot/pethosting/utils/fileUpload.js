const fs = require('fs');
const path = require('path');

// Save profile photo and return filename
exports.saveProfilePhoto = async (file) => {
  if (!file) return null;
  return file.filename;
};

// Save ad photos and return array of filenames
exports.saveAdPhotos = async (files) => {
  if (!files || files.length === 0) return [];
  return files.map(file => file.filename);
};

// Delete file if it exists
exports.deleteFile = async (filename) => {
  if (!filename) return;
  
  const filePath = path.join(process.env.UPLOAD_PATH || './uploads', filename);
  
  try {
    if (fs.existsSync(filePath)) {
      fs.unlinkSync(filePath);
    }
  } catch (error) {
    console.error('Error deleting file:', error);
  }
};