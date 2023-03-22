const database = require("../database");

getUsers = () => {
  const query = "SELECT * FROM users";
  return new Promise((resolve, reject) => {
    database.query(query, (error, result) => {
      if (error) reject(error);
      resolve(result);
    });
  });
};
