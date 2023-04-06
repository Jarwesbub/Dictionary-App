package com.example.dictionary_app

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
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
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvApiStatus.text = ""
        resetAll()

        val firstFav = binding.buFavourite0
        firstFav.tag = "off"
        firstFav.setOnClickListener {
            addWordToFavourite(firstFav)
        }

        val secondFav = binding.buFavourite1
        secondFav.tag = "off"
        secondFav.setOnClickListener {
            addWordToFavourite(secondFav)
        }

        val thirdFav = binding.buFavourite2
        thirdFav.tag = "off"
        thirdFav.setOnClickListener {
            addWordToFavourite(thirdFav)
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

        resetFavouriteButton(binding.buFavourite0)
        resetFavouriteButton(binding.buFavourite1)
        resetFavouriteButton(binding.buFavourite2)

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
                binding.tvApiStatus.text = "Status: Connection Failed"
            }
        }
    }

    private fun updateUITest(request: ApiRequest) {
        runOnUiThread {
            kotlin.run {
                if (request.data.isNotEmpty()) {
                    binding.tvApiStatus.text = "Status: OK"
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
                    binding.tvApiStatus.text = "Status: Word not found"
                }
            }
        }
    }

    private fun createLayout(index: Int?, japWord: String?, japReading: String?, englishDefinition: String?) {
        val maxWordWidth = 4
        val maxReadingWidth = 6
        var word = japWord
        var reading = japReading

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

        if(index===0) {
            binding.tvApiJapWord0.text = word
            binding.tvApiJapReading0.text = reading
            binding.tvApiJapRomaji0.text = Wanakana.toRomaji(reading.toString())
            binding.tvApiEngDefinition0.text = englishDefinition
            binding.llFirstWord.isVisible = true

        } else if(index===1) {
            binding.tvApiJapWord1.text = word
            binding.tvApiJapReading1.text = reading
            binding.tvApiJapRomaji1.text = Wanakana.toRomaji(japReading.toString())
            binding.tvApiEngDefinition1.text = englishDefinition
            binding.llSecondWord.isVisible = true
        }
        else {
            binding.tvApiJapWord2.text = word
            binding.tvApiJapReading2.text = reading
            binding.tvApiJapRomaji2.text = Wanakana.toRomaji(japReading.toString())
            binding.tvApiEngDefinition2.text = englishDefinition
            binding.llThirdWord.isVisible = true
        }
    }


    private fun resetFavouriteButton(favButton: ImageButton) {
        favButton.tag = "off"
        favButton.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                android.R.drawable.btn_star_big_off
            )
        )
    }

    private fun addWordToFavourite(favButton: ImageButton) {

            if (favButton.tag==="off") {         // Add to favorites list
                favButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        android.R.drawable.btn_star_big_on
                    )
                )
                favButton.tag="on"
            } else {                            // Remove from favourites list
                favButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        android.R.drawable.btn_star_big_off
                    )
                )
                favButton.tag="off"
            }
        }

    }