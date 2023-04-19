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
                    //println("BUFFERING")
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
                //println("COUNT: ${fileList.count()}")
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

        }catch(e: FileNotFoundException) {
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

        context.openFileOutput(fileName, Context.MODE_APPEND).use { output ->
            output.write(newWord.toByteArray())
            output.close()
        }

    }

    fun removeFromInternalStorage(englishDefinition: String) {
        fileBody.remove(englishDefinition)
        clearAllDataFromInternalStorage()

        context.openFileOutput(fileName, Context.MODE_APPEND).use { output ->
            for (map in fileBody) {
                val word = "${map.key}:${map.value[0]}:${map.value[1]}:${map.value[2]};"
                output.write(word.toByteArray())
                println(word)
            }
            output.close()
        }
    }

    fun getAllDataFromInternalStorage(): Map<String, Array<String>> {
        return fileBody
    }

    fun clearAllDataFromInternalStorage() {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
            output.write("".toByteArray())
            output.close()
        }
    }

    fun checkIfDataIsOnTheList(value :String) :Boolean {
        if(fileBody.containsKey(value)){
            return true
        }
        return false
    }

}