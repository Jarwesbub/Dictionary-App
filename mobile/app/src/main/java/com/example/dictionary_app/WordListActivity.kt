package com.example.dictionary_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import com.example.dictionary_app.databinding.ActivitySearchBinding
import com.example.dictionary_app.databinding.ActivityWordListBinding

class WordListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordListBinding
    var gridView: GridView? = null
    // Testing
    private val wordsList = arrayOf(
        "Hello", "Thanks", "Goodbye", "House", "Sun", "Moon"

    )

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)
        gridView = findViewById(R.id.gridview1)
        var adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, wordsList
        )
        gridView!!.adapter = adapter
        gridView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            // Testing
            Toast.makeText(
                applicationContext,
                (v as TextView).text, Toast.LENGTH_SHORT
            ).show()
        }
    }


}
