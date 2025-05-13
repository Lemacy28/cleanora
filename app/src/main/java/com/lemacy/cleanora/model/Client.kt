package com.lemacy.cleanora.model

data class Client(
    val id: String=""  ,       // Unique ID for the client (can be Firebase UID or custom ID)
    val name: String=""  ,     // Client's name
    val location: String="",    // Client's location
    val phoneNumber: String ="" // Client's phone number
)
