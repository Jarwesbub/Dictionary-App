const mysql = require("mysql2");
require("dotenv").config();

const config = {
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME
};

const database = mysql.createConnection(config);

database.connect(function (err) {
  if (err) {
    console.error(`Error connecting: ${err.stack}`);
    return;
  }
  console.log(`Connected as thread id ${database.threadId}`);
});

module.exports = database;
