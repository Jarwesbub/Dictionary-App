const express = require("express");
const router = express.Router();
const user = require("../model/user_model");

router.get("/", async (req, res) => {
  try {
    const response = await user.getUsers();
    console.log(response)
    res.status(200).json(response);
  } catch (error) {
    console.error(error);
    res.sendStatus(500);
  }
});

module.exports = router;
