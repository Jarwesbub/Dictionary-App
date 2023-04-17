package com.example.dictionary_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dictionary_app.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnLogin.setOnClickListener {onLoginClick()}

        binding.btnCreateUser.setOnClickListener { onCreateUserClick() }
    }

    private fun onCreateUserClick(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun onLoginClick(){

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

    private fun parseTokens(tokens: String){
        // Parse JSON string using Gson into an instance of Token class
        val tokenData = Gson().fromJson(tokens, TokenData::class.java)
        val accessToken = tokenData.accessToken
        val refreshToken = tokenData.refreshToken
        println(accessToken)
        println(refreshToken)
        prefs.writeTokens(accessToken,refreshToken)

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
            // Send JSON string to parseTokens function
            parseTokens(response)
            withContext(Dispatchers.Main) {
                showToastMessage("LOGIN SUCCESS")
            }
        } else {
            // Request unsuccessful - print an error message with the response code
            println("Error: $responseCode")
            withContext(Dispatchers.Main) {
                showToastMessage("THERE WAS AN ERROR: $responseCode")
            }
        }

        // Disconnect the connection to free up system resources
        conn.disconnect()
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }
}