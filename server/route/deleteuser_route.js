const express = require("express");
const router = express.Router();
const userModel = require("../model/user_model");
const auth = require("../utils/auth");
const login = require("../model/login_model");

router.delete("/", async (req, res) => {
    await auth.verifyAccessToken(req, res);
    const username = req.user.username;
    const userId = req.user.userId;
    if (req.user != null && req.body.username == req.user.username) {
    try {
          if (userId != null) {
              userModel.deleteUser(userId);
              res.status(200).send("User deleted");
              console.log("user account "+username+" deleted");
          } else {        
          res.status(500).send("Failed to delete user");
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