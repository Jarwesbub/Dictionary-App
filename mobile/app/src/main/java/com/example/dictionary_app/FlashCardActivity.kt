package com.example.dictionary_app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Question(val kanji: String, val meaning: String)

data class Kanji(
    val grade: Int,
    val wk_meanings: List<String>,
    val wk_readings_on: List<String>,
    val wk_readings_kun: List<String>,
    val wk_level: Int
)

data class KanjiEntry(
    val kanjiChar: String,
    val kanji: Kanji
)


class FlashCardActivity : AppCompatActivity() {
    private val questionList = mutableListOf(
        Question("一", "one"),
        Question("茶", "tea"),
        Question("日", "day"),
        Question("月", "month")
    )

    private val kanjiList1 = mutableListOf<KanjiEntry>() //Grades 1-2
    private val kanjiList2 = mutableListOf<KanjiEntry>() //Grades 3-4
    private val kanjiList3 = mutableListOf<KanjiEntry>() //Grades 5+

    private var currentQuestionIndex = 0
    private var points = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card)

        var nextButton = findViewById<Button>(R.id.buttonNext)
        val questionText = findViewById<TextView>(R.id.textViewFlashcard)

        val answerEditText =findViewById<EditText>(R.id.etAnswerInput)
        val pointsTextView = findViewById<TextView>(R.id.textViewPoints)

        updateQuestion()

        //Read the json file into variable from assets folder
        getKanjiFromJson(applicationContext)


        /*nextButton.setOnClickListener {
            val userAnswer = answerEditText.text.toString()
            val currentQuestion = questionList[currentQuestionIndex]
            if (userAnswer == currentQuestion.meaning) {
                points++
                pointsTextView.text = "Points: $points"
                questionList.removeAt(currentQuestionIndex)
            }
            if (questionList.isEmpty()) {
                nextButton.isEnabled = false
                questionText.text = "You win!"
            } else {
                updateQuestion()
            }
            answerEditText.setText("")
        }*/
        nextButton.setOnClickListener {
            if (kanjiList1.isNotEmpty()) {
                val currentKanji = kanjiList1.first()
                questionText.text = "${currentKanji.kanjiChar}: ${currentKanji.kanji.wk_meanings}"
                kanjiList1.removeAt(0)
            } else {
                questionText.text = "No more kanji in list 1"
                nextButton.isEnabled = false
            }
        }






        answerEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val editText = v as EditText
                if (editText.tag == null) {
                    editText.setText("")
                    editText.tag = "clear"
                }
            }
        }
    }

    private fun updateQuestion() {
        currentQuestionIndex = (0 until questionList.size).random()
        val currentQuestion = questionList[currentQuestionIndex]
        val questionText = findViewById<TextView>(R.id.textViewFlashcard)
        questionText.text = currentQuestion.kanji
    }

    private fun getKanjiFromJson(context: Context) {
        val jsonString = applicationContext.assets.open("kanji-wanikani.json")
            .bufferedReader().use {
                it.readText()
            }


        val kanjiMapType = object : TypeToken<Map<String, Kanji>>() {}.type
        val kanjiMap: Map<String, Kanji> = Gson().fromJson(jsonString, kanjiMapType)
        val kanjiList = mutableListOf<KanjiEntry>()
        for ((kanjiChar, kanjiData) in kanjiMap) {
            val kanjiMapEntry = KanjiEntry(kanjiChar, kanjiData)
            kanjiList.add(kanjiMapEntry)
        }
        for (kanjiEntry in kanjiList) {
            when (kanjiEntry.kanji.grade) {
                in 1..2 -> kanjiList1.add(kanjiEntry)
                in 3..4 -> kanjiList2.add(kanjiEntry)
                else -> kanjiList3.add(kanjiEntry)
            }
        }

    }
}