package com.example.dictionary_app

import android.app.Application

val prefs: Prefs by lazy {
    App.prefs!!
}

class App: Application() {

    companion object{
        var prefs: Prefs? = null
        lateinit var instance: App
        private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = Prefs(applicationContext)
    }
}