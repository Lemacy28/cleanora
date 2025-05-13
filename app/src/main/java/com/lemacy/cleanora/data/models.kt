package com.lemacy.cleanora.data

data class Profile(
    val name: String = "",
    val age: String = "", // Use Int if validation ensures it’s numeric
    val skills: String = "",
    val location: String = "",
    val phoneNumber: String = ""
) {
}