const jwt = require("jsonwebtoken");
require("dotenv").config();

generateAccessToken = (userId, username) => {
  const payload = { userId, username };
  return jwt.sign(payload, process.env.ACCESS_TOKEN_SECRET, {
    expiresIn: "15m",
  });
};

generaRefreshToken = (userId, username) => {
  const payload = { userId, username };
  return jwt.sign(payload, process.env.REFRESH_TOKEN_SECRET);
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
    throw new Error("Invalid refresh token");
  }
};

module.exports = {
  generateAccessToken,
  generaRefreshToken,
  verifyAccessToken,
  verifyRefreshToken,
};
