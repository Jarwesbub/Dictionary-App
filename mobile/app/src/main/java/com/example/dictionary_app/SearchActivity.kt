package com.example.dictionary_app

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.example.dictionary_app.databinding.ActivitySearchBinding
import com.google.gson.Gson
import dev.esnault.wanakana.core.Wanakana
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


var inputWord: (String?) = null;

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvApiStatus.text = ""
        resetAllText()

    }

    private fun resetAllText(){
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
        binding.liLayoutFirstWord.setBackgroundColor(Color.WHITE)
        binding.liLayoutSecondWord.setBackgroundColor(Color.WHITE)
        binding.liLayoutThirdWord.setBackgroundColor(Color.WHITE)
    }

    fun onClickGetFromApi(view: View?) {
        var inputText:EditText = findViewById(R.id.etApiTest)

        if(inputText.text.isNotEmpty()) {
            inputWord = inputText.text.toString()
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
                    resetAllText()

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

    private fun createLayout(index: Int?, word: String?, reading: String?, englishDefinition: String?) {

        if(index===0) {
            binding.tvApiJapWord0.text = word
            binding.tvApiJapReading0.text = reading
            binding.tvApiJapRomaji0.text = Wanakana.toRomaji(reading.toString())
            binding.tvApiEngDefinition0.text = englishDefinition
            binding.liLayoutFirstWord.setBackgroundColor(Color.LTGRAY)

        } else if(index===1) {
            binding.tvApiJapWord1.text = word
            binding.tvApiJapReading1.text = reading
            binding.tvApiJapRomaji1.text = Wanakana.toRomaji(reading.toString())
            binding.tvApiEngDefinition1.text = englishDefinition
            binding.liLayoutSecondWord.setBackgroundColor(Color.LTGRAY)

        }
        else {
            binding.tvApiJapWord2.text = word
            binding.tvApiJapReading2.text = reading
            binding.tvApiJapRomaji2.text = Wanakana.toRomaji(reading.toString())
            binding.tvApiEngDefinition2.text = englishDefinition
            binding.liLayoutThirdWord.setBackgroundColor(Color.LTGRAY)
        }
    }

}