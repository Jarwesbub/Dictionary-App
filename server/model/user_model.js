const database = require("../utils/database");

getUsers = () => {
  const query = "SELECT * FROM users";
  return new Promise((resolve, reject) => {
    database.query(query, (error, result) => {
      if (error) reject(error);
      resolve(result);
    });
  });
};

getUserByName = (username) => {
  const query = "SELECT * FROM users WHERE user_name=?";
  return new Promise((resolve, reject) => {
    database.query(query, [username], (error, result) => {
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

deleteUser = (userId) => {
  const query = "DELETE * FROM users WHERE user_id=?";
  return new Promise((resolve, reject) => {
    database.query(query, [userId], (error, result) => {
      if (error) reject(error);
      resolve(result);
    });
  });
};

UpdateUser = (userId, username, password) => {
  const query = "UPDATE users SET user_name=?, user_password=? WHERE user_id=?";
  return new Promise((resolve, reject) => {
    database.query(query, [username, password, userId], (error, result) => {
      if (error) reject(error);
      resolve(result);
    });
  });
};

module.exports = {
  getUsers,
  getUserByName,
  createUser,
  deleteUser,
  UpdateUser,
};
