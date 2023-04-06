package com.example.dictionary_app

import android.R
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dictionary_app.databinding.ActivityFlashCardBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.esnault.wanakana.core.Wanakana
import android.view.View

data class Question(val kanji: String, val meaning: String, val reading: String)

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

    private lateinit var binding: ActivityFlashCardBinding;

    private val questionList = mutableListOf(
        Question("一", "one", "ichi"),
        Question("茶", "tea", "ni"),
        Question("日", "day", "san"),
        Question("月", "month", "yon")
    )

    private val kanjiList1 = mutableListOf<KanjiEntry>() //Grades 1-2
    private val kanjiList2 = mutableListOf<KanjiEntry>() //Grades 3-4
    private val kanjiList3 = mutableListOf<KanjiEntry>() //Grades 5+

    private var currentQuestionIndex = 0
    private var points = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashCardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        var nextButton = binding.buttonNext
        val questionText = binding.textViewFlashcard

        val answerEditText = binding.etAnswerInput
        val pointsTextView = binding.textViewPoints

        updateQuestion()

        //Read the json file into variable from assets folder
        getKanjiFromJson(applicationContext)

        val spinner = binding.spnrDifficulty
        val items = arrayOf("Easy", "Medium", "Hard")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        spinner.apply {
            adapter = spinnerAdapter
            prompt = ""
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Set the selected kanji list based on the selected difficulty level
                    val selectedKanjiList = when (position) {
                        0 -> kanjiList1
                        1 -> kanjiList2
                        2 -> kanjiList3
                        else -> emptyList() // handle any other positions, if needed
                    }

                    // Clear the existing questions and add the new ones from the selected kanji list
                    questionList.clear()
                    questionList.addAll(selectedKanjiList.map {
                        Question(
                            kanji = it.kanjiChar,
                            meaning = it.kanji.wk_meanings.firstOrNull() ?: "",
                            reading = it.kanji.wk_readings_kun.firstOrNull() ?: ""
                        )
                    })

                    // Show the first question and reset points
                    points = 0
                    pointsTextView.text = "Points: $points"
                    updateQuestion()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }
        }



        nextButton.setOnClickListener {
            val userAnswer = answerEditText.text.toString()
            val currentQuestion = questionList[currentQuestionIndex]
            if (userAnswer.equals(currentQuestion.meaning, ignoreCase = true)) {
                points++
                pointsTextView.text = "Points: $points"
                questionList.removeAt(currentQuestionIndex)
            }
            if (questionList.isEmpty() || points >= 5) {
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
        val questionText = binding.textViewFlashcard
        val questionReadingText = binding.textViewFlashcardReading
        questionText.text = "${currentQuestion.kanji}: ${currentQuestion.meaning}"
        //For reasons that aren't related to this project, some of the readings have ! which is removed here
        val reading = Wanakana.toRomaji(currentQuestion.reading).replace("!", "")
        questionReadingText.text = reading
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