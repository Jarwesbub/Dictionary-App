const jwt = require("jsonwebtoken");
require("dotenv").config();

generateAccessToken = (username) => {
  return jwt.sign(username, process.env.ACCESS_TOKEN_SECRET, {
    expiresIn: "15m",
  });
};

generaRefreshToken = (username) => {
  return jwt.sign(username, process.env.REFRESH_TOKEN_SECRET);
};

verifyAccessToken = (token) => {
  try {
    const decode = jwt.verify(token, process.env.ACCESS_TOKEN_SECRET);
    return decode;
  } catch (error) {
    console.error(error);
    throw new Error("Invalid access token");
  }
};
verifyRefreshToken = (token) => {
    try {
      const decoded = jwt.verify(token, process.env.REFRESH_TOKEN_SECRET);
      return decoded;
    } catch (error) {
      console.error(error);
      throw new Error('Invalid refresh token');
    }
  }

module.exports = {
  generateAccessToken,
  generaRefreshToken,
  verifyAccessToken,
  verifyRefreshToken
};
