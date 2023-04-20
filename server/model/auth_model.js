const database = require("../utils/database");

saveRefreshToken = (userId, refreshToken) => {
  const user_Id = userId;
  const refresh_Token = refreshToken;
  const issuedAt = new Date();
  const expiresAt = new Date();
  expiresAt.setDate(expiresAt.getDate() + 30);

  const query = `INSERT INTO refresh_tokens (user_id, refresh_token, issued_at, expires_at) VALUES (?, ?, ?, ?)`;
  return new Promise((resolve, reject) => {
    database.query(
      query,
      [user_Id, refresh_Token, issuedAt, expiresAt],
      (error, result) => {
        if (error) reject(error);
        resolve(result);
      }
    );
  });
};
getRefreshToken = (userId) => {
  const user_Id = userId;

  const query = "SELECT refresh_token FROM refresh_tokens WHERE user_id=?;";
  return new Promise((resolve, reject) => {
    database.query(query, [user_Id], (error, result) => {
      if (error) reject(error);
      else if (result.length === 0){
        resolve(null)
      }
      else{
      resolve(result);}
    });
  });
};
updateRefreshToken = (userId, refreshToken) => {
  const user_Id = userId;
  const refresh_Token = refreshToken;
  const issuedAt = new Date();
  const expiresAt = new Date();
  expiresAt.setDate(expiresAt.getDate() + 30);

  const query = `UPDATE refresh_tokens SET refresh_token = ?, issued_at = ?, expires_at = ? WHERE user_id = ?`;
  return new Promise((resolve, reject) => {
    database.query(
      query,
      [refresh_Token, issuedAt, expiresAt, user_Id],
      (error, result) => {
        if (error) reject(error);
        resolve(result);
      }
    );
  });
};

module.exports = {
  saveRefreshToken,
  getRefreshToken,
  updateRefreshToken,
};
