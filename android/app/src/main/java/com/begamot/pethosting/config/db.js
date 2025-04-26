const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');

// Set mongoose options
mongoose.set('strictQuery', false);

const connectDB = async () => {
  try {
    let mongoUri;

    // Use MONGODB_URI from environment if available, otherwise use in-memory database
    if (process.env.MONGODB_URI) {
      mongoUri = process.env.MONGODB_URI;
    } else {
      // Create an in-memory MongoDB instance
      const mongod = await MongoMemoryServer.create();
      mongoUri = mongod.getUri();
      console.log('Using in-memory MongoDB instance');
    }

    // Connect to the database
    const conn = await mongoose.connect(mongoUri);
    console.log(`MongoDB Connected: ${conn.connection.host}`);
  } catch (error) {
    console.error(`Error connecting to MongoDB: ${error.message}`);
    process.exit(1);
  }
};

module.exports = connectDB;