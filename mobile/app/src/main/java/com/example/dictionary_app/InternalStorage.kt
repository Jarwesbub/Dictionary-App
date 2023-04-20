package com.example.dictionary_app

import android.content.Context
import java.io.FileNotFoundException

// Saves and handles data located in internal storage
// Keeps all the data in a mutable list

class InternalStorage (val context: Context) {
    private var fileBody = mutableMapOf<String, Array<String>>() // Map that mimics the data in internal storage
    private val fileName = "favourites.text" // File name for internal storage

    // Start function for Internal Storage -> sets the fileBody mutable map ready for use
    fun setInternalStorage() { // Must be initialized first
        // Opens a file by name from internal storage located in phone's memory
        // Cuts the loaded text row in a String array
        try {
            fileBody.clear()
            var fileList = ArrayList<String>()
            context.openFileInput(fileName).use { stream ->
                stream.bufferedReader().use {
                    var cache = ""
                    for (text in it.readText()) {
                        val line = text.toString()
                        // Separates the string values to array by ";"
                        if (line.contains(";")) {
                            val group = cache + line.substringBefore(";")
                            fileList.add(group)
                            cache = ""
                        } else {
                            cache += line
                        }
                    }
                }
            }
                // Separates the string values to array by ":"
                for (line in fileList) {
                    val row = line.split(":")
                    if(row.count()>=4) {
                        val japWords = arrayOf(row[1], row[2], row[3])
                        fileBody[row[0]] = japWords
                    }
                }

        } catch(e: FileNotFoundException) { // Creates a new text file for data (if file not found)
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write("".toByteArray())
                output.close()
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }

    }

    // Writes given data to internal storage and adds the values to fileBody map for faster data load for user
    fun writeToInternalStorage(englishDefinition: String, japWord: String, japReading: String, japRomaji: String) {
        val array = arrayOf(japWord,japReading,japRomaji)
        // Combines all the String values to one
        // : = Separates the array value
        // ; = Finnish line
        val newWord: String = "${englishDefinition}:${japWord}:${japReading}:${japRomaji};"
        fileBody[englishDefinition] = array

        // Adds up the String value to the file (MODE_APPEND)
        try {
            context.openFileOutput(fileName, Context.MODE_APPEND).use { output ->
                output.write(newWord.toByteArray())
                output.close()
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }

    }

    // Removes the given key value from the internal storage's file and fileBody map
    fun removeFromInternalStorage(englishDefinition: String) {
        fileBody.remove(englishDefinition)  // Removes a word from fileBody by the key value
        clearAllDataFromInternalStorage()   // Clears all the data located in the text file

        // Overwrites all the data in fileBody to internal storage's text file
        try {
            context.openFileOutput(fileName, Context.MODE_APPEND).use { output ->
                for (map in fileBody) {
                    val word = "${map.key}:${map.value[0]}:${map.value[1]}:${map.value[2]};"
                    output.write(word.toByteArray())
                    println(word)
                }
                output.close()
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    // Gets all the data from internal storage as a map
    fun getAllDataFromInternalStorage(): Map<String, Array<String>> {
        return fileBody
    }

    // Clear all the data from the internal storage and fileBody map
    fun clearAllDataFromInternalStorage() {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write("".toByteArray())
                output.close()
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }

    }

    // Checks if given value is found from the map
    fun checkIfValueByKeyIsOnTheMap(key :String, value: String) :Boolean {
        val array = fileBody[key]
        if (array != null && array.contains(value)) {
            return true
        }
        return false
    }

}