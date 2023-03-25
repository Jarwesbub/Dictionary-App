const database = require("../database");

getAllUsers = () => {
  const query = "SELECT user_name FROM users";
  return new Promise((resolve, reject) => {
    database.query(query, (error, result) => {
      if (error) reject(error);
      resolve(result);
    });
  });
};

createUser = (username, password) => {
  const query = "INSERT INTO users (user_name, user_password) VALUES (?,?)";
  return new Promise((resolve, reject) => {
    database.query(query, [username, password], (error, result) => {
      if (error) reject(error);
      resolve(result);
    });
  });
};

module.exports = {
  getAllUsers,
  createUser,
};
