const flashcardRouter = require('express').Router();
var flashcardjsonFile = require('../data/kanji-wanikani.json');

flashcardRouter.get('/', (req, res) => {
    res.json(flashcardjsonFile);
});

module.exports = flashcardRouter;