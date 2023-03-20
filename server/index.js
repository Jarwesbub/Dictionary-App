const express = require("express");

const port = 3000;

const app = express();

app.get("/", (req, res) => {
  res.status(200).send({ message: "Kukkuluuruu" });
  res.end;
});

app.listen(port, () => {
  console.log(`Server listening on port ${port}...`);
});
