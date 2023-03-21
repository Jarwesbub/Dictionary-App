package com.example.dictionary_app

class ApiDataObject (
       var slug: String,
       var is_common: Boolean,
       var japanese: Array<ApiDataObjectJapanese>,
       var senses: Array<ApiDataObjectSenses>,
       )

class ApiDataObjectJapanese (
       var word: String,
       var reading: String,
        )

class ApiDataObjectSenses (
       var english_definitions: Array<String>,
        )
