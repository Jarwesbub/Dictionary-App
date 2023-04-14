package com.example.dictionary_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.dictionary_app.databinding.ActivityWordListBinding

class WordListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordListBinding
    private val internalStorage: InternalStorage = InternalStorage(this)
    private var wordsMap = mutableMapOf<String, Array<String>>()
    private var englishWords = ArrayList<String>()
    private var gridView: GridView? = null
    private var currentWord: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)
        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvShowcaseEngDefinition.text = ""
        binding.tvShowcaseJapWord.text = ""
        binding.tvShowcaseJapReading.text = ""
        binding.tvShowcaseJapRomaji.text = ""
        binding.tvShowcaseJapRomaji.text = ""

        internalStorage.setInternalStorage()
        wordsMap = internalStorage.getAllDataFromInternalStorage().toMutableMap()

        for (map in wordsMap) {
            val word = map.key
            englishWords.add(word)
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, englishWords
        )
        gridView = binding.gvEnglishWordsList
        gridView!!.adapter = adapter
        gridView!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, word, position, id ->
                val englishWord: String = (word as TextView).text.toString()
                setShowcaseText(englishWord)
            }

        val items = resources.getStringArray(R.array.favourite_dropdown_items)
        val arrAdapter = ArrayAdapter(this, R.layout.dropdown_item, items)
        val dropdownMenu = binding.autoCompleteTextView
        dropdownMenu.setAdapter(arrAdapter)
        dropdownMenu.apply {
            onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    when (position) {
                        0 -> removeWord()
                        1 -> removeAllWords()
                    }
                }
        }
    }

    private fun setShowcaseText(englishWord: String) {
        val japWord = wordsMap.getValue(englishWord).elementAt(0)
        val japReading = wordsMap.getValue(englishWord).elementAt(1)
        val japRomaji = wordsMap.getValue(englishWord).elementAt(2)

        binding.tvShowcaseJapWord.text = japWord
        binding.tvShowcaseJapReading.text = japReading
        binding.tvShowcaseJapRomaji.text = japRomaji
        binding.tvShowcaseEngDefinition.text = englishWord
        currentWord=englishWord
    }

    private fun removeWord() {
        if(currentWord!==null) {
            internalStorage.removeFromInternalStorage(currentWord.toString())
            val index = englishWords.indexOf(currentWord)
            englishWords.remove(currentWord)
            println("REMOVING FROM INDEX: $index")
            updateGridViewItems()
            wordsMap.remove(currentWord)
            showToastText("Word removed")
        } else {
            showToastText("Select a word from the list first")
        }
    }

    private fun removeAllWords() {
        internalStorage.clearAllDataFromInternalStorage()
        englishWords.clear()
        updateGridViewItems()
        wordsMap.clear()
        showToastText("List cleared successfully")
    }

    private fun showToastText(value: String) {
        Toast.makeText(
            applicationContext,
            value, Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateGridViewItems() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, englishWords
        )
        gridView!!.adapter = adapter
    }
}