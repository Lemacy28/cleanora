package com.lemacy.cleanora.model

data class Cleaner(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val age: String = "",
    val skills: String ="",
    val location: String = "",
    val role: String = "cleaner",
    val phoneNumber: String = "",
    val availability: Boolean = false,

)
