package com.example.dictionary_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //declare switch from layout
        val swDark = findViewById<SwitchMaterial>(R.id.swDarkMode)
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

        //check if dark mode is enabled and set switch to true if it is
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            swDark.setChecked(true)
        }
    }
}