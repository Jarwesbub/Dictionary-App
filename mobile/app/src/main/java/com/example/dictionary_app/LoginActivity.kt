package com.example.dictionary_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dictionary_app.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnLogin.setOnClickListener {onLoginClick()}
    }

    fun onLoginClick(){

        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        println(username)
        println(password)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = loginRequest(username, password)
                // Handle the successful response here
                println(result)
            } catch (e: Exception) {
                // Handle the error here
                println(e.message)

            }
        }

    }
    private suspend fun loginRequest(username: String, password: String) = withContext(Dispatchers.IO){
        // Create a URL object with the URL we want to connect to
        val url = URL("http://{server ip here}:3000/login")

        // Open an HTTP connection to the URL
        val conn = url.openConnection() as HttpURLConnection

        // Set the request method to POST, and enable output and input for the connection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.doInput = true

        // Set the Content-Type header to indicate that we're sending a JSON payload
        conn.setRequestProperty("Content-Type", "application/json")

        // Create a JSON payload with the username and password
        val body = "{ \"username\": \"$username\", \"password\": \"$password\" }"

        // Write the payload to the output stream of the connection
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
}