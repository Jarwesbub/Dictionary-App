const database = require("../utils/database");

postHighScore = (user_id, score) => {
    const query = "INSERT INTO high_scores (user_id, score) VALUES(?,?)";
    return new Promise((resolve, reject) => {
      database.query(query, [user_id, score], (error, result) => {
        if (error) reject(error);
        resolve(result);
      });
    });
};

updateHighScore = (user_id, score) => {
    const query = "UPDATE high_scores SET score = ? WHERE user_id = ?";
    return new Promise((resolve, reject) => {
        database.query(query, [score, user_id], (error, result) => {
            if (error) reject(error);
            resolve(result);
        });
    });
};

getHighScore = (user_id) => {
    const query = "SELECT * FROM high_scores WHERE user_id=?";
    return new Promise((resolve, reject) => {
        database.query(query, [user_id], (error, result) => {
            if (error) reject(error);
            resolve(result);
        });
    });
};
  
module.exports = {
    postHighScore,
    getHighScore,
    updateHighScore
};