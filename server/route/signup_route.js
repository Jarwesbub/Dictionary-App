const express = require("express");
const router = express.Router();
const bcrypt = require("bcrypt")
const user = require("../model/user_model");

router.post("/", async (req,res)=>{
    try {
        const salt = await bcrypt.genSalt()
        const hashedPassword = await bcrypt.hash(req.body.password, salt)

        const username = req.body.username;

        const response = await user.createUser(username,hashedPassword)
        res.status(200).json(response);
    }
    catch (error) {
        console.error(error);
        res.sendStatus(500);
      }
})

module.exports = router;