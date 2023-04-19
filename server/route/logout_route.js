const express = require("express");
const router = express.Router();
const authModel = require("../model/auth_model");
const login = require("../model/login_model");

router.post("/", async (req, res) => {
    const username = req.body.username;
    const refreshToken = req.body.refresh_token;
    console.log(refreshToken);

    try {
      const response = await login.getUserInfo(username);
      if (response != null) {
        const userId = response[0].user_id;
        if (userId != null) {
            authModel.deleteRefreshTokens(userId);
            res
            .status(200)
            .send("Token deleted");
        } else {        
        res
        .status(500)
        .send("Failed to delete tokens");
        }
      } else {
        res.status(401).send("Not allowed");
      }
    } catch (error) {
      console.error(error);
      res.sendStatus(500);
    }
  });
  
  module.exports = router;