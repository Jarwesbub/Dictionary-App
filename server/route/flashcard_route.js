const flashcardRouter = require('express').Router();
const flashcard = require('../model/flashcard_model');
var flashcardjsonFile = require('../data/kanji-wanikani.json');


flashcardRouter.get('/', async(req, res) => {
    res.json(flashcardjsonFile);
});

flashcardRouter.put('/highscore', async (req, res) => {
    const user_id = req.body.user_id;
    const score = req.body.score;
    try{
        const response = await flashcard.getHighScore(user_id);
        if(response.length == 0){
            const response = await flashcard.postHighScore(user_id, score);
            res.status(200).json(response);
        } else if(response[0].score < score){
            const response = await flashcard.updateHighScore(user_id, score);
            res.status(200).json(response);
        } else {
            res.sendStatus(400);
        }
    } catch(error) {
        console.error(error);
        res.sendStatus(500);
    }
});

flashcardRouter.post('/highscore', async (req, res) => {
    const user_id = req.body.user_id;
    const score = req.body.score;
    try{
        const response = await flashcard.postHighScore(user_id, score);
        res.status(200).json(response);
    }
    catch(error) {
        console.error(error);
        res.sendStatus(500);
    }
});

module.exports = flashcardRouter;