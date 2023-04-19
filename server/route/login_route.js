const express = require("express");
const router = express.Router();
const bcrypt = require("bcrypt");
const login = require("../model/login_model");
const auth = require("../utils/auth");

router.post("/", async (req, res) => {
  const username = req.body.username;
  const password = req.body.password;

  try {
    const response = await login.getUserInfo(username);
    const hashedPassword = response[0].user_password;
    if (await bcrypt.compare(password, hashedPassword)) {
      const userId = response[0].user_id;
      const username = response[0].user_name;
      const accessToken = auth.generateAccessToken(userId, username);
      const refreshToken = await auth.generaRefreshToken(userId, username);
      res.status(200).json({
        user_id: userId,
        user_name: username,
        accessToken: accessToken,
        refreshToken: refreshToken,
      });
    } else {
      res.status(401).send("Not allowed");
    }
  } catch (error) {
    console.error(error);
    res.sendStatus(500);
  }
});

router.post("/token", async (req, res) => {
  const refreshToken = req.body.refreshToken;
  try {
    const response = await auth.verifyRefreshToken(refreshToken);
    const userId = response.userId;
    const username = response.username;
    const accessToken = await auth.generateAccessToken(userId, username);
    const newRefreshToken = await auth.generaRefreshToken(userId, username);
    res.status(200).json({
      user_id: userId,
      user_name: username,
      accessToken: accessToken,
      refreshToken: newRefreshToken,
    });
  } catch (error) {
    if (error.message === "Invalid refresh token") {
      res.status(401).send("Invalid refresh token");
    } else {
      console.error(error);
      res.status(500).send("Internal server error");
    }
  }
});

module.exports = router;
