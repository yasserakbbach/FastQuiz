package com.ressay.fastquiz.data.models

data class User(
    val email : String = "",
    val password : String = "",
    var username : String = "",
    var score : Long = 0L
) {

    companion object {

        fun generateUsername(email : String) = email.substring(0, email.indexOfFirst { p -> p == '@' })
    }

    fun increaseScore(score : Int) {
        this.score += score
    }
}