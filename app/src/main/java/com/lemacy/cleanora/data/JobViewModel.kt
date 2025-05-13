package com.lemacy.cleanora.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.lemacy.cleanora.model.Client
import com.lemacy.cleanora.model.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class JobViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _clientJobs = MutableStateFlow<List<Job>>(emptyList())
    val clientJobs: StateFlow<List<Job>> = _clientJobs


    private val auth = Firebase.auth

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _jobPostSuccess = MutableStateFlow(false)
    val jobPostSuccess = _jobPostSuccess.asStateFlow()

    private val _jobPostError = MutableStateFlow<String?>(null)
    val jobPostError = _jobPostError.asStateFlow()

    val clientId = Firebase.auth.currentUser?.uid ?: ""


    private val _acceptedJobs = MutableStateFlow<List<Job>>(emptyList())
    val acceptedJobs: StateFlow<List<Job>> = _acceptedJobs

    // Fetch available jobs from Firestore
    suspend fun fetchAvailableJobs() {
        _isLoading.value = true
        try {
            val result = db.collection("jobs").get().await()
            val jobList = result.documents.mapNotNull { it.toObject(Job::class.java)?.copy(id = it.id) }
            _jobs.value = jobList
            _isLoading.value = false
        } catch (e: Exception) {
            _error.value = e.localizedMessage
            _isLoading.value = false
        }
    }

    // Post a new job to Firestore
    fun postJob(title: String, description: String, location: String, price: Double) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _jobPostError.value = "User not authenticated"
            return
        }

        _isLoading.value = true

        val newJob = Job(
            title = title,
            description = description,
            location = location,
            price = price,
            clientId = userId  // 👈 This is the key change
        )

        db.collection("jobs")
            .add(newJob)
            .addOnSuccessListener {
                _isLoading.value = false
                _jobPostSuccess.value = true
                _jobPostError.value = null
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _jobPostSuccess.value = false
                _jobPostError.value = e.localizedMessage
            }
    }

    fun acceptJob(jobId: String) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("jobs").document(jobId)
            .update("acceptedBy", uid)
            .addOnSuccessListener {
                viewModelScope.launch {
                    fetchAvailableJobs()   // ✅ suspend called safely
                    fetchAcceptedJobs()    // ✅ suspend called safely
                }
            }
            .addOnFailureListener {
                _error.value = it.message
            }

    }
        fun fetchAcceptedJobs() {
            val uid = auth.currentUser?.uid ?: return

            db.collection("jobs")
                .whereEqualTo("acceptedBy", uid)
                .get()
                .addOnSuccessListener { result ->
                    val jobList = result.documents.mapNotNull {
                        it.toObject(Job::class.java)?.copy(id = it.id)
                    }
                    _acceptedJobs.value = jobList
                }
                .addOnFailureListener {
                    _error.value = it.message
                }
        }
    fun removeAcceptedJob(jobId: String) {
        firestore.collection("jobs").document(jobId)
            .delete()
            .addOnSuccessListener {
                fetchAcceptedJobs()
            }
            .addOnFailureListener { exception ->
                _error.value = exception.message
            }
    }
    fun loadJobsForClient(clientId: String) {
        db.collection("jobs")
            .whereEqualTo("clientId", clientId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val jobs = snapshot.documents.mapNotNull { it.toObject(Job::class.java) }
                    _clientJobs.value = jobs
                }
            }
    }
    fun deleteJob(jobId: String) {
        db.collection("jobs").document(jobId)
            .delete()
            .addOnSuccessListener { Log.d("JobViewModel", "Job deleted successfully") }
            .addOnFailureListener { e -> Log.w("JobViewModel", "Error deleting job", e) }
    }


}











