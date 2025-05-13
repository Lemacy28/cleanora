package com.lemacy.cleanora.model


data class Job(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val clientId: String = "",
    val price: Double = 0.0,
    val postedBy: String = ""
)
