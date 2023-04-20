const express = require("express");
const router = express.Router();
const userModel = require("../model/user_model");
const login = require("../model/login_model");

router.delete("/", async (req, res) => {
    const username = req.body.username;
    const refreshToken = req.body.refresh_token;
    console.log(refreshToken);
    
    try {
      const response = await login.getUserInfo(username);
      if (response != null) {
        const userId = response[0].user_id;
        if (userId != null) {
            userModel.deleteUser(userId);
            res
            .status(200)
            .send("User deleted");
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