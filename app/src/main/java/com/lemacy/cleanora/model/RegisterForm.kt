package com.lemacy.cleanora.model


data class RegisterForm(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "", // "client" or "cleaner"
    val location: String = "",
    val phoneNumber: String = "",
    val skills: List<String> = emptyList()
)


