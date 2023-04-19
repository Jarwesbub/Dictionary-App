package com.example.dictionary_app

import android.content.Context
import java.io.FileNotFoundException

class InternalStorage (val context: Context) {
    private var fileBody = mutableMapOf<String, Array<String>>()
    private val fileName = "favourites.text"

    fun setInternalStorage() { // Must be initialized first
        try {
            fileBody.clear()
            var fileList = ArrayList<String>()
            context.openFileInput(fileName).use { stream ->
                stream.bufferedReader().use {
                    var cache = ""
                    for (text in it.readText()) {
                        val line = text.toString()
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

                for(s in fileList){
                    println(s)
                }

                for (line in fileList) {
                    val row = line.split(":")
                    if(row.count()>=4) {
                        val japWords = arrayOf(row[1], row[2], row[3])
                        fileBody[row[0]] = japWords
                    }
                }

        } catch(e: FileNotFoundException) { // Creates new text file for data
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write("".toByteArray())
                output.close()
            }
        }catch(e: Exception){
            e.printStackTrace()
        }

    }

    fun writeToInternalStorage(englishDefinition: String, japWord: String, japReading: String, japRomaji: String) {
        val array = arrayOf(japWord,japReading,japRomaji)
        val newWord: String = "${englishDefinition}:${japWord}:${japReading}:${japRomaji};"
        fileBody[englishDefinition] = array

        try {
            context.openFileOutput(fileName, Context.MODE_APPEND).use { output ->
                output.write(newWord.toByteArray())
                output.close()
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }

    }

    fun removeFromInternalStorage(englishDefinition: String) {
        fileBody.remove(englishDefinition)
        clearAllDataFromInternalStorage()

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

    fun getAllDataFromInternalStorage(): Map<String, Array<String>> {
        return fileBody
    }

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

    fun checkIfKeyIsOnTheMap(key :String) :Boolean {
        if(fileBody.containsKey(key)) {
            return true
        }
        return false
    }

}