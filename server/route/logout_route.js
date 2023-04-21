const express = require("express");
const router = express.Router();
const auth = require("../utils/auth");
const authModel = require("../model/auth_model");
const login = require("../model/login_model");

router.post("/", async (req, res) => {
    await auth.verifyAccessToken(req, res);
    const username = req.user.username;
    const userId = req.user.userId;
    if (req.user != null && req.body.username == req.user.username) {
    try {
        if (userId != null) {
            authModel.deleteRefreshTokens(userId);
            res.status(200).send("Success");
            console.log("refresh token of user "+username+" deleted");
        } else {        
        res.status(500).send("Failed to delete tokens");
        }
      } catch (error) {
      console.error(error);
      res.sendStatus(500);
    }
  } else {
    res.status(401).send("Not allowed");
  }
});
  
  module.exports = router;