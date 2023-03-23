package com.example.dictionary_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.w3c.dom.Text

data class Question(val kanji: String, val meaning: String)

class FlashCard : AppCompatActivity() {
    private val questionList = mutableListOf(
        Question("一", "one"),
        Question("茶", "tea"),
        Question("日", "day"),
        Question("月", "month")
    )

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

        nextButton.setOnClickListener {
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
}