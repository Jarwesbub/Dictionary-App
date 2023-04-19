package com.example.dictionary_app

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context){
    // Initialize a SharedPreferences object with the name "preferences"
    // and set it to private mode to ensure that only the application can access it
    private val  preferences: SharedPreferences = context.getSharedPreferences("preferences",Context.MODE_PRIVATE)

    // Create an editor for the SharedPreferences object
    private val editor = preferences.edit()

    // Save tokens to SharedPreferences
    fun writeTokens(accessToken: String, refreshToken: String){
        editor.putString("accessToken",accessToken)
        editor.putString("refreshToken",refreshToken)
        println("saved tokens")
    }
    // Retrieve the access token from SharedPreferences. If there is no token return noToken
    fun getAccessToken(): String? {
        return preferences.getString("accessToken", "noToken")
    }
    // Retrieve the refresh token from SharedPreferences. If there is no token return noToken
    fun getRefreshToken(): String? {
        return preferences.getString("refreshToken","noToken")
    }

    // Clear shared preferences for logging out user
    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.clear()
        editor.commit()
    }
}