const database = require("../utils/database");

getUserInfo = (username) => {
    const query = "SELECT * FROM users WHERE user_name=?"
    return new Promise((resolve, reject) => {
      database.query(query, [username], (error, result) => {
        if (error) reject(error);
        resolve(result);
      });
    });
  };
  
  module.exports ={
    getUserInfo
  }