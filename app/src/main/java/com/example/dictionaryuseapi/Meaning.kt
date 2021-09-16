package com.example.dictionaryuseapi

data class Meaning(
    val definitions: List<Definition>,
    val partOfSpeech: String
)