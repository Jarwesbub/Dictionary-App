const express = require("express");
const router = express.Router();
const bcrypt = require("bcrypt");
const signup = require("../model/signup_model");

// This is a route that handles POST requests to create a new user account
router.post("/", async (req, res) => {
  try {
    const username = req.body.username;

    // Get list of all users in database
    const userList = await signup.getAllUsers();

    // Check if given username exists in database
    const isUsernameTaken = userList.some(
      (user) => user.user_name === username
    );
    // If the username is taken, it sends a 409 (Conflict) response with the message "name taken."
    if (isUsernameTaken) {
      res.status(409).send("name taken.");
    }
    // If the username is not taken, proceed with signup by hashing provided password and updating database with user records
    else {
      const salt = await bcrypt.genSalt();
      const hashedPassword = await bcrypt.hash(req.body.password, salt);

      const response = await signup.createUser(username, hashedPassword);
      res.status(200).json(response);
    }
  } catch (error) {
    // If there is an error during the process, it sends a 500 (Internal Server Error) response and logs the error message to the console.
    console.error(error);
    res.sendStatus(500);
  }
});

module.exports = router;
