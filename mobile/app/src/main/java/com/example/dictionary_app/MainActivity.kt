package com.example.dictionary_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
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

                updateUI(request)
                inputStreamReader.close()
                inputSystem.close()
            }
            else
            {
                binding.tvApiEngDefinition.text = "Connection Failed"
            }

        }
    }

    private fun updateUI(request: ApiRequest) {
        runOnUiThread {
            kotlin.run {
                binding.tvApiStatus.text = request.meta.toString()
                binding.tvApiJapWord.text = request.data[0].slug.toString()
                binding.tvApiJapReading.text = ""
                binding.tvApiEngDefinition.text = ""
                //println("Request data is:")
                //println(request.data[0].slug.toString())

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