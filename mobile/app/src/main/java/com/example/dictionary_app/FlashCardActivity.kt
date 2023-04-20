package com.example.dictionary_app

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dictionary_app.databinding.ActivityFlashCardBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.esnault.wanakana.core.Wanakana
import kotlinx.coroutines.*
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


data class Question(val kanji: String, val meaning: String, val reading: String)

data class HighscoreResponse(val userId: String, val score: Int)

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

    private val questionList = mutableListOf<Question>()

    private val kanjiList1 = mutableListOf<KanjiEntry>() //Grades 1-2
    private val kanjiList2 = mutableListOf<KanjiEntry>() //Grades 3-4
    private val kanjiList3 = mutableListOf<KanjiEntry>() //Grades 5+

    private var currentQuestionIndex = 0
    private var points = 0
    private var attempts = 3
    private var highScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashCardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //UI Bindings
        var nextButton = binding.buttonNext
        val questionText = binding.textViewFlashcard
        val hintText = binding.textViewFlashcardHint
        val readingText = binding.textViewFlashcardReading
        val answerEditText = binding.etAnswerInput
        val pointsTextView = binding.textViewPoints
        val attemptsTextView = binding.textViewAttempts

        GlobalScope.launch {
            val kanjiList = getKanjiFromJson(applicationContext)
            for (kanjiEntry in kanjiList) {
                when (kanjiEntry.kanji.grade) {
                    in 1..2 -> kanjiList1.add(kanjiEntry)
                    in 3..4 -> kanjiList2.add(kanjiEntry)
                    else -> kanjiList3.add(kanjiEntry)
                }
            }
            loadDefaultQuestionList()
            getHighscore(applicationContext, Integer.parseInt(prefs.getIdName()))
        }

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
                        //Selects the most appropriate reading, the non standard readings are marked with ! in json,
                        //so this finds the first non ! mark reading to use.
                        val reading = if (it.kanji.wk_readings_kun.isNotEmpty()) {
                            val kunReading = it.kanji.wk_readings_kun.firstOrNull { r -> !r.startsWith("!") }
                            if (kunReading != null) {
                                kunReading
                            } else {
                                val onReading = it.kanji.wk_readings_on.firstOrNull { r -> !r.startsWith("!") }
                                onReading ?: it.kanji.wk_readings_on[0]
                            }
                        } else {
                            it.kanji.wk_readings_on.firstOrNull { r -> !r.startsWith("!") }
                                ?: it.kanji.wk_readings_on[0]
                        }

                        Question(
                            kanji = it.kanjiChar,
                            meaning = it.kanji.wk_meanings.firstOrNull() ?: "",
                            reading = reading
                        )
                    })

                    //Reset UI
                    nextButton.isEnabled = true
                    questionText.textSize = 132F
                    // Show the first question and reset points, attempts
                    attempts = 3
                    points = 0
                    pointsTextView.text = "Points: $points"
                    updateQuestion()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    loadDefaultQuestionList()
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
            } else {
                attempts -= 1
                attemptsTextView.text = "Attempts: $attempts"
            }
            if (questionList.isEmpty()) {
                nextButton.isEnabled = false
                questionText.text = "You Won!"
                questionText.textSize = 72F
                hintText.text = ""
                readingText.text = ""
                GlobalScope.launch {
                    val userId = prefs.getIdName()
                    putHighscore(applicationContext, Integer.parseInt(userId), points)
                    getHighscore(applicationContext, Integer.parseInt(userId))
                }
            } else if(attempts == 0) {
                nextButton.isEnabled = false
                questionText.text = "You Lost, reached score of $points"
                questionText.textSize = 52F
                hintText.text = ""
                readingText.text = ""
                GlobalScope.launch {
                    val userId = prefs.getIdName()
                    putHighscore(applicationContext, Integer.parseInt(userId), points)
                    getHighscore(applicationContext, Integer.parseInt(userId))
                }
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

        questionText.setOnClickListener {
            hintText.visibility = View.VISIBLE
        }
    }

    private fun updateQuestion() {
        if (questionList.isEmpty()) {
            // handle empty list case
        } else {
            currentQuestionIndex = (0 until questionList.size).random()
            val currentQuestion = questionList[currentQuestionIndex]
            val questionText = binding.textViewFlashcard
            val questionReadingText = binding.textViewFlashcardReading
            val hintText = binding.textViewFlashcardHint
            val reading = Wanakana.toRomaji(currentQuestion.reading)

            //Because this code gets called from coroutine we need to run UI update on UI thread
            //UI can't be updated from background thread.
            runOnUiThread {
                questionText.text = "${currentQuestion.kanji}"
                questionReadingText.text = reading
                hintText.visibility = View.INVISIBLE
                hintText.text = makeHintString(currentQuestion.meaning)
            }
        }
    }

    private suspend fun getKanjiFromJson(context: Context): List<KanjiEntry> {
        return withContext(Dispatchers.IO) { // launch a coroutine on the IO thread
            val jsonString =
                URL("http://10.0.2.2:3000/flashcard/").readText() // perform network operation
            val kanjiMapType = object : TypeToken<Map<String, Kanji>>() {}.type
            val kanjiMap: Map<String, Kanji> = Gson().fromJson(jsonString, kanjiMapType)
            val kanjiList = mutableListOf<KanjiEntry>()
            for ((kanjiChar, kanjiData) in kanjiMap) {
                val kanjiMapEntry = KanjiEntry(kanjiChar, kanjiData)
                kanjiList.add(kanjiMapEntry)
            }
            kanjiList
        }
    }

    private suspend fun putHighscore(context: Context, user_id: Int, score: Int) {
        // Create a URL object with the URL we want to connect to
        val url = URL("http://10.0.2.2:3000/flashcard/highscore")


        // Open an HTTP connection to the URL
        val conn = url.openConnection() as HttpURLConnection

        // Set the request method to POST, and enable output and input for the connection
        conn.requestMethod = "PUT"
        conn.doOutput = true
        conn.doInput = true

        // Set the Content-Type header to indicate that we're sending a JSON payload
        conn.setRequestProperty("Content-Type", "application/json")

        // Create a JSON payload with the username and password
        val body = "{ \"user_id\": \"$user_id\", \"score\": \"$score\" }"

        val output = OutputStreamWriter(conn.outputStream)
        output.write(body)
        output.flush()

        // Check the response code to see if the request was successful
        val responseCode = conn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Request successful - read the response data into a string and print it to the console
            val response = conn.inputStream.bufferedReader().use { it.readText() }
            println(response)
        } else {
            // Request unsuccessful - print an error message with the response code
            println("Error: $responseCode")
        }

        // Disconnect the connection to free up system resources
        conn.disconnect()
    }

    private suspend fun getHighscore(context: Context, user_id: Int) {
        val url = URL("http://10.0.2.2:3000/flashcard/highscore/$user_id")
        return withContext(Dispatchers.IO) { // launch a coroutine on the IO thread
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                val gson = Gson()
                val dataArray = gson.fromJson(response, Array<HighscoreResponse>::class.java)
                if (dataArray.isNotEmpty()) {
                    val score = dataArray[0].score
                    println(score)
                    connection.disconnect()
                    withContext(Dispatchers.Main) {
                        val highscoreTextView = binding.textViewHighscore
                        highscoreTextView.text = "Highscore of $score points"
                    }
                } else {
                    throw IOException("Response array is empty")
                }
            } else {
                throw IOException("HTTP error code: $responseCode")
            }
        }
    }

    private fun makeHintString(hint: String): String {
        val formattedHint = StringBuilder()
        formattedHint.append(hint.first())
        for (i in 1 until hint.length - 1) {
            if (hint[i] == ' ') {
                formattedHint.append(" ")
            } else if (i == 8 && hint.length > 9) {
                formattedHint.append(hint[i])
            } else {
                formattedHint.append("_ ")
            }
        }
        formattedHint.append(hint.last())
        return formattedHint.toString()
    }

    private fun loadDefaultQuestionList() {
        if (questionList.isEmpty()) {
            questionList.addAll(kanjiList1.map {
                val reading = if (it.kanji.wk_readings_kun.isNotEmpty()) {
                    val kunReading = it.kanji.wk_readings_kun.firstOrNull { r -> !r.startsWith("!") }
                    if (kunReading != null) {
                        kunReading
                    } else {
                        val onReading = it.kanji.wk_readings_on.firstOrNull { r -> !r.startsWith("!") }
                        onReading ?: it.kanji.wk_readings_on[0]
                    }
                } else {
                    it.kanji.wk_readings_on.firstOrNull { r -> !r.startsWith("!") }
                        ?: it.kanji.wk_readings_on[0]
                }

                Question(
                    kanji = it.kanjiChar,
                    meaning = it.kanji.wk_meanings.firstOrNull() ?: "",
                    reading = reading
                )
            })
            updateQuestion()
        }
    }
}
