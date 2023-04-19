package com.example.dictionary_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //declare switch from layout
        val swDark = findViewById<SwitchMaterial>(R.id.swDarkMode)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnDeleteUser = findViewById<Button>(R.id.btnDeleteUser)
        //listen for switch state change
        swDark.setOnCheckedChangeListener {_, isChecked ->

            //change between light and dark
            if (swDark.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                swDark.text = "Dark Mode: Enabled"
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                swDark.text = "Dark Mode: Disabled"
            }
        }

        btnLogout.setOnClickListener{
            val username = prefs.getUserName().toString()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val result = logoutRequest(username)
                    // Handle the successful response here
                    println(result)
                } catch (e: Exception) {
                    // Handle the error here
                    println(e.message)

                }
            }
            //clear tokens from preferences
            prefs.clearSharedPreference()
            updateTextViews()
            Toast.makeText(this,"LogOut.",Toast.LENGTH_SHORT).show()

            returnToLogin()
        }

        btnDeleteUser.setOnClickListener{
            Toast.makeText(this,"Delete user",Toast.LENGTH_SHORT).show()
            prefs.printTokens()
            updateTextViews()

            //returnToLogin()
        }

        //check if dark mode is enabled and set switch to true if it is
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            swDark.isChecked = true
        }
        updateTextViews()
    }
    fun updateTextViews() {
        val usrView = findViewById<TextView>(R.id.usrView)
        val accView = findViewById<TextView>(R.id.accView)
        val refView = findViewById<TextView>(R.id.refView)
        usrView.text = "UserName: " + prefs.getUserName()
        accView.text = "AccessToken: " + prefs.getAccessToken()
        refView.text = "RefreshToken: " + prefs.getRefreshToken()
    }

    fun returnToLogin(){
        //return to login screen and close activities
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private suspend fun logoutRequest(username: String) = withContext(Dispatchers.IO){
        // Create a URL object with the URL we want to connect to
        val url = URL("http://10.0.2.2:3000/logout")

        // Open an HTTP connection to the URL
        val conn = url.openConnection() as HttpURLConnection

        // Set the request method to POST, and enable output and input for the connection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.doInput = true

        // Set the Content-Type header to indicate that we're sending a JSON payload
        conn.setRequestProperty("Content-Type", "application/json")

        // Create a JSON payload with the username
        val body = "{ \"username\": \"$username\"}"

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
            withContext(Dispatchers.Main) {
                showToastMessage("Logout Success")
            }
        } else {
            // Request unsuccessful - print an error message with the response code
            println("Error: $responseCode")
            withContext(Dispatchers.Main) {
                showToastMessage("Error: $responseCode")
            }
        }

        // Disconnect the connection to free up system resources
        conn.disconnect()
    }
    private fun showToastMessage(message: String) {
        Toast.makeText(this@SettingsActivity, message, Toast.LENGTH_SHORT).show()
    }
}