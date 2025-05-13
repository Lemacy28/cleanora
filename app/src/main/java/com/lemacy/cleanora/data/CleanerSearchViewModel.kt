package com.lemacy.cleanora.data

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.lemacy.cleanora.model.Cleaner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CleanerSearchViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _cleaners = MutableStateFlow<List<Cleaner>>(emptyList())
    val cleaners: StateFlow<List<Cleaner>> = _cleaners

    private val _isFetchingCleaners = MutableStateFlow(false)
    val isFetchingCleaners: StateFlow<Boolean> = _isFetchingCleaners

    private val _cleanerError = MutableStateFlow<String?>(null)
    val cleanerError: StateFlow<String?> = _cleanerError


    fun loadCleaners(searchQuery: String = "") {
        firestore.collection("users")
            .whereEqualTo("role", "cleaner") // Filter by role (cleaner)
            .get()
            .addOnSuccessListener { result ->
                val filtered = result.documents.mapNotNull { doc ->
                    doc.toObject(Cleaner::class.java)?.copy(id = doc.id)
                }.filter {
                    // Additional search query filters
                    it.availability &&
                            (searchQuery.isBlank() ||
                                    it.name.contains(searchQuery, ignoreCase = true) ||
                                    it.location.contains(searchQuery, ignoreCase = true) ||
                                    it.skills.contains(searchQuery, ignoreCase = true)
                                    )
                }
                _cleaners.value = filtered
            }
    }
    fun fetchAllCleaners() {
        _isFetchingCleaners.value = true
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("role", "cleaner")
            .get()
            .addOnSuccessListener { result ->
                val cleanerList = result.documents.mapNotNull { it.toObject(Cleaner::class.java) }
                _cleaners.value = cleanerList
                _isFetchingCleaners.value = false
            }
            .addOnFailureListener { exception ->
                _cleanerError.value = exception.message
                _isFetchingCleaners.value = false
            }

    }
    fun acceptCleaner(clientId: String, cleaner: Cleaner, onComplete: (Boolean) -> Unit) {
        val cleanerData = mapOf(
            "name" to cleaner.name,
            "email" to cleaner.email,
            "skills" to cleaner.skills,
            "location" to cleaner.location
        )

        firestore.collection("clients")
            .document(clientId)
            .collection("accepted_cleaners")
            .document(cleaner.id) // Make sure Cleaner has a unique ID
            .set(cleanerData)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

}
