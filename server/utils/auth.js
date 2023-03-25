const jwt = require("jsonwebtoken");
const authModel = require("../model/auth_model");
require("dotenv").config();

generateAccessToken = (userId, username) => {
  const payload = { userId, username };
  return jwt.sign(payload, process.env.ACCESS_TOKEN_SECRET, {
    expiresIn: "15m",
  });
};

generaRefreshToken = async (userId, username) => {
  try {
    const payload = { userId, username };
    const refreshToken = jwt.sign(payload, process.env.REFRESH_TOKEN_SECRET, {
      expiresIn: "30d",
    });

    // Check if there is an existing refresh token for the user in the database
    const existingToken = await authModel.getRefreshToken(userId);

    if (existingToken) {
      // If there is an existing token, update the row with the new token and expiration date
      await authModel.updateRefreshToken(userId, refreshToken);
    } else {
      // If there is no existing token, insert a new row with the new token and expiration date
      await authModel.saveRefreshToken(userId, refreshToken);
    }

    return refreshToken;
  } catch (error) {
    console.error(error);

    throw new Error("Error generating refresh token");
  }
};

verifyAccessToken = (req, res, next) => {
  // Get the authorization header from the request
  const authHeader = req.headers["authorization"];
  const token = authHeader && authHeader.split(" ")[1];
  if (token == null) {
    return res.sendStatus(401);
  }
  // Verify the token using the secret key

  jwt.verify(token, process.env.ACCESS_TOKEN_SECRET, (err, user) => {
    if (err) {
      return res.sendStatus(403);
    }
    // Set the user object on the request for use in subsequent middleware and routes
    req.user = user;
    next();
  });
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
