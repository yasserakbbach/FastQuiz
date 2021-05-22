package com.ressay.fastquiz.data.models

data class Quiz(
    val question : String = "",
    val options : List<String> = listOf(),
    val answer : Int = 1
)