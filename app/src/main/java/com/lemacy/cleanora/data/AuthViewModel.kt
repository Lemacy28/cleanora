package com.lemacy.cleanora.data




import android.adservices.ondevicepersonalization.UserData
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemacy.cleanora.model.Cleaner
import com.lemacy.cleanora.model.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUser = MutableStateFlow<Cleaner?>(null)
    val currentUser: StateFlow<Cleaner?> = _currentUser

    private val _currentClient = MutableStateFlow<Client?>(null)
    val currentClient: StateFlow<Client?> = _currentClient

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _cleaners = MutableStateFlow<List<Cleaner>>(emptyList())
    val cleaners: StateFlow<List<Cleaner>> = _cleaners

    private val _isFetchingCleaners = MutableStateFlow(false)
    val isFetchingCleaners: StateFlow<Boolean> = _isFetchingCleaners

    private val _cleanerError = MutableStateFlow<String?>(null)
    val cleanerError: StateFlow<String?> = _cleanerError

    private val _profileUpdated = MutableStateFlow(false)
    val profileUpdated: StateFlow<Boolean> = _profileUpdated

    private val _profileUpdateError = MutableStateFlow<String?>(null)
    val profileUpdateError: StateFlow<String?> = _profileUpdateError

    fun registerUser(
        name: String,
        email: String,
        password: String,
        role: String,
        phoneNumber: String,
        onSuccess: () -> Unit
    ) {
        _isLoading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: return@addOnSuccessListener
                val userData = hashMapOf(
                    "uid" to userId,
                    "name" to name,
                    "email" to email,
                    "role" to role,
                    "phoneNumber" to phoneNumber
                )
                db.collection("users").document(userId).set(userData)
                    .addOnSuccessListener {
                        _userRole.value = role
                        _isLoading.value = false
                        onSuccess()
                    }
                    .addOnFailureListener {
                        _authError.value = it.message
                        _isLoading.value = false
                    }
            }
            .addOnFailureListener {
                _authError.value = it.message
                _isLoading.value = false
            }
    }

    fun loginUser(email: String, password: String, onSuccess: (String) -> Unit) {
        _isLoading.value = true

        // Hardcoded admin credentials check
//        if (email == "admin@cleanora.com" && password == "admin123") {
//            _userRole.value = "admin"
//            _isLoading.value = false
//            onSuccess("admin")
//            return
//        }

        // Regular user login
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        val role = document.getString("role") ?: "unknown"
                        _userRole.value = role
                        _isLoading.value = false
                        onSuccess(role)
                    }
                    .addOnFailureListener {
                        _authError.value = it.message
                        _isLoading.value = false
                    }
            }
            .addOnFailureListener {
                _authError.value = it.message
                _isLoading.value = false
            }
    }

    fun logout(onLogout: () -> Unit) {
        auth.signOut()
        _userRole.value = null
        onLogout()
    }

    fun setError(message: String) {
        _authError.value = message
    }

    fun updateProfile(name: String, age: String, skills: String, location: String, phoneNumber: String) {
        val userId = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "name" to name,
            "age" to age,
            "skills" to skills,
            "location" to location,
            "phoneNumber" to phoneNumber
        )

        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                // Manually update local state
                _currentUser.value = Cleaner(name, age, skills, location, phoneNumber)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Cleaner profile update failed: ${e.message}")
                _profileUpdateError.value = e.message
            }
    }


    fun updateClientProfile(name: String, location: String, phoneNumber: String) {
        val userId = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "name" to name,
            "location" to location,
            "phoneNumber" to phoneNumber
        )

        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                val updatedClient = Client(name, location, phoneNumber)
                _currentClient.value = updatedClient
                _profileUpdated.value = true
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Client profile update failed: ${e.message}")
                _profileUpdateError.value = e.message
            }
    }

    fun loadCurrentUserData() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(Cleaner::class.java)
                    _currentUser.value = user
                }
            }
            .addOnFailureListener {
                _profileUpdateError.value = it.message
            }
    }

    fun loadCurrentClientData() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val client = document.toObject(Client::class.java)
                    _currentClient.value = client
                }
            }
            .addOnFailureListener {
                _profileUpdateError.value = it.message
            }
    }

    fun resetProfileUpdateFlag() {
        _profileUpdated.value = false
    }

}


//    fun fetchAllCleaners() {
//        _isFetchingCleaners.value = true
//        FirebaseFirestore.getInstance()
//            .collection("users")
//            .whereEqualTo("role", "cleaner")
//            .get()
//            .addOnSuccessListener { result ->
//                val cleanerList = result.documents.mapNotNull { it.toObject(Cleaner::class.java) }
//                _cleaners.value = cleanerList
//                _isFetchingCleaners.value = false
//            }
//            .addOnFailureListener { exception ->
//                _cleanerError.value = exception.message
//                _isFetchingCleaners.value = false
//            }
//    }




