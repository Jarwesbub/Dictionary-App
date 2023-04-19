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


var inputWord: (String?) = null

class SearchActivity : AppCompatActivity() {
    private val internalStorage: InternalStorage = InternalStorage(this)
    private lateinit var binding: ActivitySearchBinding
    private lateinit var firstWord: List<String>
    private lateinit var secondWord: List<String>
    private lateinit var thirdWord: List<String>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resetAll()
        internalStorage.setInternalStorage()

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

    fun onClickGetFromApi(view: View?) {
        var inputText:EditText = findViewById(R.id.etApiTest)

        if(inputText.text.isNotEmpty()) {
            inputWord = inputText.text.toString().lowercase()
            fetchJsonData().start()
        }
    }

    private fun fetchJsonData(): Thread {
        return Thread {
            val url = URL("https://jisho.org/api/v1/search/words?keyword=$inputWord")
            val connection = url.openConnection() as HttpsURLConnection

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val request = Gson().fromJson(inputStreamReader, ApiRequest::class.java)

                updateUITest(request)
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

    private fun updateUITest(request: ApiRequest) {
        runOnUiThread {
            kotlin.run {
                if (request.data.isNotEmpty()) {
                    resetAll()

                    var index = 2
                    if(request.data.size-1<index) {
                        index = request.data.size -1
                    }
                    for(n in 0..index) {
                        val word: String = request.data[n].japanese[0].word
                        val reading: String = request.data[n].japanese[0].reading
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

    private fun createLayout(index: Int, japWord: String, japReading: String, englishDefinition: String) {
        val maxWordWidth = 4
        val maxReadingWidth = 6
        var word = japWord
        var reading = japReading
        var favButton: ImageButton

        if(japWord != null && japWord.length>maxWordWidth) {
            word = ""
            val wordArray = japWord.chunked(maxWordWidth)
            for(w in wordArray) {
                word += w + "\n"
            }
        }

        if(japReading != null && japReading.length>maxWordWidth) {
            reading = ""
            val readingArray = japReading.chunked(maxReadingWidth)
            for(w in readingArray) {
                reading += w + "\n"
            }
        }

        var romaji = Wanakana.toRomaji(reading)

        if(index===0) {
            firstWord = listOf(englishDefinition,
                checkIfNullValue(japWord),
                checkIfNullValue(japReading),
                checkIfNullValue(romaji))

            binding.tvApiJapWord0.text = word
            binding.tvApiJapReading0.text = reading
            binding.tvApiJapRomaji0.text = romaji
            binding.tvApiEngDefinition0.text = englishDefinition
            binding.llFirstWord.isVisible = true
            favButton = binding.buFavourite0

        } else if(index===1) {
            secondWord = listOf(englishDefinition,
                checkIfNullValue(japWord),
                checkIfNullValue(japReading),
                checkIfNullValue(romaji))

            binding.tvApiJapWord1.text = word
            binding.tvApiJapReading1.text = reading
            binding.tvApiJapRomaji1.text = romaji
            binding.tvApiEngDefinition1.text = englishDefinition
            binding.llSecondWord.isVisible = true
            favButton = binding.buFavourite1
        }
        else {
            thirdWord = listOf(englishDefinition,
                checkIfNullValue(japWord),
                checkIfNullValue(japReading),
                checkIfNullValue(romaji))

            binding.tvApiJapWord2.text = word
            binding.tvApiJapReading2.text = reading
            binding.tvApiJapRomaji2.text = romaji
            binding.tvApiEngDefinition2.text = englishDefinition
            binding.llThirdWord.isVisible = true
            favButton = binding.buFavourite2
        }
        val isFavButtonActive = internalStorage.checkIfDataIsOnTheList(englishDefinition)
        setFavouriteButton(favButton, isFavButtonActive)
    }

    private fun checkIfNullValue(value: String):String {
        if(value===null){
            println("NULL VALUE")
            return " "
        } else{
            return value
        }
    }

    private fun setFavouriteButton(favButton: ImageButton, setOn: Boolean) {
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun buttonAddWordToFavourite(index: Int, favButton: ImageButton) {
            if (favButton.tag==="off") {         // Add to favorites list
                saveToStorage(index)
                setFavouriteButton(favButton, true)
                Toast.makeText(
                    applicationContext,
                    "Added to the favourites", Toast.LENGTH_SHORT
                ).show()
            } else {                            // Remove from favourites list
                removeFromStorage(index)
                setFavouriteButton(favButton, false)
                Toast.makeText(
                    applicationContext,
                    "Removed from the favourites", Toast.LENGTH_SHORT
                ).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun saveToStorage(index: Int){
        if(index===0) {
            internalStorage.writeToInternalStorage(firstWord[0],firstWord[1],firstWord[2],firstWord[3])
        } else if (index===1) {
            internalStorage.writeToInternalStorage(secondWord[0],secondWord[1],secondWord[2],secondWord[3])
        } else {
            internalStorage.writeToInternalStorage(thirdWord[0],thirdWord[1],thirdWord[2],thirdWord[3])
        }
    }
    private fun removeFromStorage(index: Int) {
        if(index===0) {
            internalStorage.removeFromInternalStorage(firstWord[0])
        } else if (index===1) {
            internalStorage.removeFromInternalStorage(secondWord[0])
        } else {
            internalStorage.removeFromInternalStorage(thirdWord[0])
        }
    }

}
