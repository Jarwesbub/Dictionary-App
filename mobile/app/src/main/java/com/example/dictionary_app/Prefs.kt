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
        editor.commit()
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

    fun saveUserData(user_id: String, user_name: String){
        editor.putString("user_id",user_id)
        editor.putString("user_name",user_name)
        editor.commit()
        println("user data saved")
    }

}