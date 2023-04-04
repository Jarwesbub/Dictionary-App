package com.example.dictionary_app

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context){
    private val  preferences: SharedPreferences = context.getSharedPreferences("preferences",Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun writeTokens(accessToken: String, refreshToken: String){
        editor.putString("accessToken",accessToken)
        editor.putString("refreshToken",refreshToken)
        println("saved tokens")
    }
    fun getAccessToken(): String? {
        return preferences.getString("accessToken", "noToken")
    }
    fun getRefreshToken(): String? {
        return preferences.getString("refreshToken","noToken")
    }

}