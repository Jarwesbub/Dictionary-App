package com.example.dictionary_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var buFlashCardActivity = findViewById<Button>(R.id.buFlashCardActivity)
        buFlashCardActivity.setOnClickListener {
            val intent = Intent(this, FlashCard::class.java)
            startActivity(intent)
        }
    }
}