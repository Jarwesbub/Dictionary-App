package com.example.dictionary_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //add a menu button on the tools bar
        menuInflater.inflate(R.menu.menubuttons, menu)
        return true
    }

    fun openWordSearch(view: View?) {
    }
    fun openFavouriteWords(view: View?) {
    }
    fun openFlashCards(view: View?) {
    }
    fun openSettings(view: View?) {
    }
}