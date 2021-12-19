package com.acilgin.composetrainingfirestore.data

data class Book(
        val name: String, val author: String, val image: String,
        val description: String,val link:String,val category: String = "aa"
) {
    constructor() : this("", "", "", "","","")
}