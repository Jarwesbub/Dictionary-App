const mysql = require("mysql");
require("dotenv").config();

const config = {
  host: "localhost",
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: "dictionary_bd"
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
