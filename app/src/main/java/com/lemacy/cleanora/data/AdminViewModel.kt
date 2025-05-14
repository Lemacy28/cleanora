package com.lemacy.cleanora.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.lemacy.cleanora.model.Job
import com.lemacy.cleanora.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _jobCount = MutableStateFlow(0)
    val jobCount: StateFlow<Int> = _jobCount

    private val _userCount = MutableStateFlow(0)
    val userCount: StateFlow<Int> = _userCount

    init {
        fetchJobs()
        fetchUsers()
    }

    fun fetchJobs() {
        viewModelScope.launch {
            db.collection("jobs")
                .get()
                .addOnSuccessListener { result ->
                    val jobList = result.documents.mapNotNull { it.toObject(Job::class.java) }
                    _jobs.value = jobList
                    _jobCount.value = jobList.size
                }
        }
    }

    fun fetchUsers() {
        viewModelScope.launch {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    val userList = mutableListOf<User>()
                    for (document in result.documents) {
                        try {
                            val user = document.toObject(User::class.java)
                            if (user != null) {
                                userList.add(user)
                            }
                        } catch (e: Exception) {
                            Log.e("AdminViewModel", "Failed to parse user: ${e.message}")
                            Log.e("AdminViewModel", "Bad user doc: ${document.data}")
                        }
                    }
                    _users.value = userList
                    _userCount.value = userList.size
                }
                .addOnFailureListener { e ->
                    Log.e("AdminViewModel", "Error fetching users: ${e.message}")
                }
        }
    }

}
