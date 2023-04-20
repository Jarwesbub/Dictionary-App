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


        val refreshToken = prefs.getRefreshToken()
        if(refreshToken!=="noToken"){
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    refreshToken?.let { refreshTokeLoginRequest(it) }
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }


        binding.btnLogin.setOnClickListener {onLoginClick()}

        binding.btnCreateUser.setOnClickListener { onCreateUserClick() }
    }
    override fun onResume() {
        super.onResume()
        val refreshToken = prefs.getRefreshToken()
        if(refreshToken!=="noToken"){
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    refreshToken?.let { refreshTokeLoginRequest(it) }
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }

    }


    private fun moveToMain(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    private fun onCreateUserClick(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun onLoginClick(){

        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                loginRequest(username, password)

            } catch (e: Exception) {
                // Handle the error here
                println(e.message)

            }
        }

    }

    private fun parseResponse(response: String){
        // Parse JSON string using Gson into an instance of TokenData class
        val tokenData = Gson().fromJson(response, TokenData::class.java)
        val accessToken = tokenData.accessToken
        val refreshToken = tokenData.refreshToken
        // Save Tokens to sharedprefs
        prefs.writeTokens(accessToken,refreshToken)
        println(accessToken)
        println(refreshToken)

        // Parse JSON string using Gson into an instance of UserData class
        val userData = Gson().fromJson(response, UserData::class.java)
        val user_id = userData.user_id
        val username = userData.user_name
        // Save userdata to sharedprefs
        prefs.saveUserData(user_id,username)


    }

    private suspend fun loginRequest(username: String, password: String) = withContext(Dispatchers.IO){
        // Create a URL object with the URL we want to connect to
        val url = URL("http://192.168.178.29:3000/login")

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
            // Send JSON string to parseResponse function
            parseResponse(response)
            withContext(Dispatchers.Main) {
                showToastMessage("LOGIN SUCCESS")
                moveToMain()
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
    private suspend fun refreshTokeLoginRequest(refreshToken: String) = withContext(Dispatchers.IO){
        // Create a URL object with the URL we want to connect to
        val url = URL("http://192.168.178.29:3000/login/token")

        // Open an HTTP connection to the URL
        val conn = url.openConnection() as HttpURLConnection

        // Set the request method to POST, and enable output and input for the connection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.doInput = true

        // Set the Content-Type header to indicate that we're sending a JSON payload
        conn.setRequestProperty("Content-Type", "application/json")

        // Create a JSON payload with the username and password
        val body = "{ \"refreshToken\": \"$refreshToken\"}"

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
            // Send JSON string to parseResponse function
            parseResponse(response)
            withContext(Dispatchers.Main) {
                moveToMain()
            }

        } else {
            // Request unsuccessful - print an error message with the response code
            println("Error: $responseCode")
        }

        // Disconnect the connection to free up system resources
        conn.disconnect()
    }





    private fun showToastMessage(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }
}