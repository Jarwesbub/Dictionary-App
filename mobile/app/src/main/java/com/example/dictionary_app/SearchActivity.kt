package com.example.dictionary_app

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.dictionary_app.databinding.ActivitySearchBinding
import com.google.gson.Gson
import dev.esnault.wanakana.core.Wanakana
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


var inputWord: String? = null

// Activity for searching and handling data from "jisho.org" API

class SearchActivity : AppCompatActivity() {
    private val internalStorage: InternalStorage = InternalStorage(this)
    private lateinit var binding: ActivitySearchBinding
    private lateinit var currentWordsList: ArrayList<List<String>>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentWordsList = ArrayList()
        resetAll()                              // Resets all the textViews and visibility
        internalStorage.setInternalStorage()    // Sets the internal storage data for ready to use

        val firstFav = binding.buFavourite0
        firstFav.tag = "off"
        firstFav.setOnClickListener {
            buttonAddWordToFavourite(0, firstFav)
        }

        val secondFav = binding.buFavourite1
        secondFav.tag = "off"
        secondFav.setOnClickListener {
            buttonAddWordToFavourite(1, secondFav)
        }

        val thirdFav = binding.buFavourite2
        thirdFav.tag = "off"
        thirdFav.setOnClickListener {
            buttonAddWordToFavourite(2, thirdFav)
        }

    }

    // Resets all the textViews and hides the words in layout
    private fun resetAll() {
        binding.tvApiJapWord0.text = ""
        binding.tvApiJapReading0.text = ""
        binding.tvApiJapRomaji0.text = ""
        binding.tvApiEngDefinition0.text = ""
        binding.tvApiJapWord1.text = ""
        binding.tvApiJapReading1.text = ""
        binding.tvApiJapRomaji1.text = ""
        binding.tvApiEngDefinition1.text = ""
        binding.tvApiJapWord2.text = ""
        binding.tvApiJapReading2.text = ""
        binding.tvApiJapRomaji2.text = ""
        binding.tvApiEngDefinition2.text = ""

        binding.llFirstWord.isVisible = false
        binding.llSecondWord.isVisible = false
        binding.llThirdWord.isVisible = false

    }

    // Button for searching the word
    fun onClickGetFromApi(view: View?) {
        val inputText:EditText = binding.etApiTest

        if(inputText.text.isNotEmpty()) {
            inputWord = inputText.text.toString().lowercase()
            fetchJsonData().start()
        }
    }

    // Function for getting data from the API
    private fun fetchJsonData(): Thread {
        return Thread {
            val url = URL("https://jisho.org/api/v1/search/words?keyword=$inputWord")
            val connection = url.openConnection() as HttpsURLConnection

            // Connected successfully
            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")

                // Creates Gson object from inputStreamReader data
                // Filters data by taking only necessary data that matches with the ApiRequest class
                val request = Gson().fromJson(inputStreamReader, ApiRequest::class.java)

                // Sets filtered request data to UI
                setAPIRequestDataToUI(request)
                inputStreamReader.close()
                inputSystem.close()
            }
            else {
                Toast.makeText(
                    applicationContext,
                    "Connection failed", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Sets UI data from the Gson object
    private fun setAPIRequestDataToUI(request: ApiRequest) {
        runOnUiThread {
            kotlin.run {
                if (request.data.isNotEmpty()) {
                    resetAll()
                    currentWordsList.clear()

                    var maxIndex = 2 // How many words we want to take from the request data by index
                    if(request.data.size-1<maxIndex) { // Checks if there is not enough data for the current index
                        maxIndex = request.data.size -1
                    }

                    for(n in 0..maxIndex) { // Loops the data to the UI and updates layouts
                        val word = request.data[n].japanese[0].word.orEmpty()
                        val reading: String = request.data[n].japanese[0].reading.orEmpty()
                        val englishDef: String = request.data[n].senses[0].english_definitions.joinToString()
                        createLayout(n, word,reading,englishDef)
                    }
                }
                else {
                    Toast.makeText(
                        applicationContext,
                        "Word not found", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Creates/updates the values to layout
    private fun createLayout(index: Int, japWord: String, japReading: String, englishDefinition: String) {
        val maxWordLength = 4       // Max width for japanese word
        val maxReadingLength = 6    // Max length for japanese reading
        var word = japWord          // Japanese word
        var reading = japReading    // Japanese reading
        var favButton: ImageButton  // Favourite button inside the layout

        // If word is too wide to handle in UI -> set lineBreaks based on max length
        if(japWord.length>maxWordLength) {
            word = setLineBreaksByLength(japWord, maxWordLength)

        }

        // If word is too wide to handle in UI -> set lineBreaks based on max length
        if(japReading.length>maxWordLength) {
            reading = setLineBreaksByLength(japReading, maxReadingLength)

        }

        var romaji: String = Wanakana.toRomaji(reading).orEmpty()   // Non-japanese reading

        // Sets all the UI texts based on current index
        when(index) {
            0 -> {
                binding.tvApiJapWord0.text = word
                binding.tvApiJapReading0.text = reading
                binding.tvApiJapRomaji0.text = romaji
                binding.tvApiEngDefinition0.text = englishDefinition
                binding.llFirstWord.isVisible = true
                favButton = binding.buFavourite0
            }
            1 -> {
                binding.tvApiJapWord1.text = word
                binding.tvApiJapReading1.text = reading
                binding.tvApiJapRomaji1.text = romaji
                binding.tvApiEngDefinition1.text = englishDefinition
                binding.llSecondWord.isVisible = true
                favButton = binding.buFavourite1
            }
            else -> {
                binding.tvApiJapWord2.text = word
                binding.tvApiJapReading2.text = reading
                binding.tvApiJapRomaji2.text = romaji
                binding.tvApiEngDefinition2.text = englishDefinition
                binding.llThirdWord.isVisible = true
                favButton = binding.buFavourite2
            }
        }

        // Adds non-filtered words to list
        currentWordsList.add(
            listOf(englishDefinition,
                japWord,
                japReading,
                romaji))

        // Sets favourite button active or nonactive
        val isFavButtonActive = internalStorage.checkIfValueByKeyIsOnTheMap(englishDefinition, romaji)
        setFavouriteButtonImage(favButton, isFavButtonActive)
    }


    // Sets favourite button's image based on tag: "on" or "off"
    private fun setFavouriteButtonImage(favButton: ImageButton, setOn: Boolean) {
        if(setOn) {
            favButton.tag = "on"
            favButton.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    android.R.drawable.btn_star_big_on
                )
            )
        }
        else {
            favButton.tag = "off"
            favButton.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    android.R.drawable.btn_star_big_off
                )
            )
        }
    }

    // Sets lineBreaks to the String value based on max length
    private fun setLineBreaksByLength(value: String, maxWidth: Int) :String {
        val valueArray = value.chunked(maxWidth)
        var newValue = ""
        for(w in valueArray) {
            newValue += w + "\n"
        }
        return newValue
    }

    // Adds word to the favourites
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun buttonAddWordToFavourite(index: Int, favButton: ImageButton) {
            if (favButton.tag==="off") {         // Add to favorites list
                saveToStorage(index)
                setFavouriteButtonImage(favButton, true)
                Toast.makeText(
                    applicationContext,
                    "Added to the favourites", Toast.LENGTH_SHORT
                ).show()
            } else {                            // Remove from favourites list
                removeFromStorage(index)
                setFavouriteButtonImage(favButton, false)
                Toast.makeText(
                    applicationContext,
                    "Removed from the favourites", Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Writes String values based on the index from internal storage
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun saveToStorage(index: Int) {
        if(currentWordsList[index] !== null) {
            val english = currentWordsList[index][0]
            val word = currentWordsList[index][1]
            val reading = currentWordsList[index][2]
            val romaji = currentWordsList[index][3]
            internalStorage.writeToInternalStorage(english, word, reading,  romaji)
        }
    }

    // Removes String values based on the index from internal storage
    private fun removeFromStorage(index: Int) {
        if(currentWordsList[index] !== null) {
            val englishDefinition = currentWordsList[index][0]
            internalStorage.removeFromInternalStorage(englishDefinition)
        }
    }

}

