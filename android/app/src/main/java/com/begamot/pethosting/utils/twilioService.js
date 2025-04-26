const twilio = require('twilio');
require('dotenv').config();

// Create Twilio client
const accountSid = process.env.TWILIO_ACCOUNT_SID;
const authToken = process.env.TWILIO_AUTH_TOKEN;
const twilioPhoneNumber = process.env.TWILIO_PHONE_NUMBER;

// Initialize client with fallback to prevent errors if not configured
let client;
if (accountSid && authToken) {
  client = twilio(accountSid, authToken);
} else {
  console.warn('Twilio credentials not found. SMS functionality disabled.');
}

// Send SMS message
exports.sendSMS = async (to, body) => {
  try {
    // If Twilio not configured, log message but don't fail
    if (!client) {
      console.log(`[MOCK SMS] To: ${to}, Message: ${body}`);
      return { 
        success: true, 
        message: 'SMS mock sent successfully'
      };
    }

    const message = await client.messages.create({
      body,
      from: twilioPhoneNumber,
      to
    });

    return {
      success: true,
      messageId: message.sid
    };
  } catch (error) {
    console.error('Error sending SMS:', error);
    
    // For development, still treat as success but log error
    if (process.env.NODE_ENV === 'development') {
      console.log(`[MOCK SMS] To: ${to}, Message: ${body}`);
      return { 
        success: true, 
        message: 'SMS mock sent successfully (error in real send)'
      };
    }
    
    throw error;
  }
};