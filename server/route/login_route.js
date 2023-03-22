const express = require("express");
const router = express.Router();
const user = require("../model/user_model");

router.post("/", async (req, res) => {

  const username = req.body.username;
  const password = req.body.password;

  try {
    const response = await user.getUserByName(username);
    res.status(200).json(response);
  } catch (error) {
    console.error(error);
    res.sendStatus(500);
  }
});

module.exports = router;
