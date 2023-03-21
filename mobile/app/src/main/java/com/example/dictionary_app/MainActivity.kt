package com.example.dictionary_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.example.dictionary_app.databinding.ActivityMainBinding
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


var inputWord: (String?) = null;

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            else
            {
                binding.tvApiStatus.text = "Status: Connection Failed"
            }

        }
    }

    private fun updateUITest(request: ApiRequest) {
        runOnUiThread {
            kotlin.run {
                binding.tvApiStatus.text = "Status: OK"
                binding.tvApiJapWord.text = request.data[0].japanese[0].word
                binding.tvApiJapReading.text = request.data[0].japanese[0].reading

                val count = request.data[0].senses[0].english_definitions.size -1

                var englishDef = ""

                for(n in 0..count) {
                    englishDef += request.data[0].senses[0].english_definitions[n]+",\n"
                }

                binding.tvApiEngDefinition.text = englishDef

            }
        }
    }

    fun onClickGetFromApi(view: View?) {
        var inputText:EditText = findViewById(R.id.etApiTest)

        if(inputText.text.isNotEmpty()) {
            inputWord = inputText.text.toString()
            fetchJsonData().start()
        }
    }
}