package com.lemacy.cleanora.ui.theme.jobsearch

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemacy.cleanora.data.AuthViewModel
import com.lemacy.cleanora.data.JobViewModel
import com.lemacy.cleanora.model.Client
import com.lemacy.cleanora.model.Job
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_HOME
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_JOB_SEARCH
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_PROFILE
import com.lemacy.cleanora.ui.theme.home.JobCard
import com.lemacy.cleanora.ui.theme.home.LemonGreen
import com.lemacy.cleanora.ui.theme.home.LightGreen

import kotlin.collections.addAll
import kotlin.text.clear

@Composable
fun CleanerJobSearchScreen(navController: NavHostController) {
    val jobViewModel: JobViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val jobs by jobViewModel.jobs.collectAsState()
    val isLoading by jobViewModel.isLoading.collectAsState()
    val error by jobViewModel.error.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedJob by remember { mutableStateOf<Job?>(null) }

    // Fetch available jobs when screen is launched
    LaunchedEffect(Unit) {
        jobViewModel.fetchAvailableJobs()
    }

    // Filter jobs based on search query
    val filteredJobs = jobs.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF7CB518))
                .padding(16.dp)
        ) {
            Text(
                text = "Find Jobs",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search jobs...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp)
        )

        // Job List
        Box(modifier = Modifier.weight(1f)) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))

                filteredJobs.isEmpty() -> Text("No matching jobs found.", modifier = Modifier.padding(16.dp))

                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredJobs) { job ->
                        JobCard(
                            job = job,
                            onViewDetailsClicked = {
                                selectedJob = job
                            }
                        )
                    }
                }
            }

            // Show Job Details Dialog if a job is selected
            selectedJob?.let { job ->
                JobDetailsDialog(
                    job = job,
                    onDismiss = { selectedJob = null },
                    onAccept = {
                        jobViewModel.acceptJob(job.id)
                        selectedJob = null
                    }
                )
            }
        }

        // Bottom Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF7CB518))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.navigate(CLEANER_HOME) {
                    popUpTo(CLEANER_HOME) { inclusive = true }
                }
            }) {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.Black)
            }
            IconButton(onClick = {
                navController.navigate(CLEANER_JOB_SEARCH)
            }) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
            }
            IconButton(onClick = {
                navController.navigate(CLEANER_PROFILE)
            }) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.Black)
            }
        }
    }
}

@Composable
fun JobCard(job: Job, onViewDetailsClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6FFF0))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = job.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onViewDetailsClicked,
                shape = RoundedCornerShape(20),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB518))
            ) {
                Text("View Details", color = Color.White)
            }
        }
    }
}

@Composable
fun JobDetailsDialog(
    job: Job,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(job.title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Title: ${job.title}")
                Text("Location: ${job.location}")
                Text("Price: ${job.price}")
                Text("Description:\n${job.description}")
            }
        },
        confirmButton = {
            Column {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB518))
                ) {
                    Text("Accept Job", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid

                        if (userId != null) {
                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    val phoneNumber = document.getString("phoneNumber")
                                    if (!phoneNumber.isNullOrEmpty()) {
                                        val formatted = if (phoneNumber.startsWith("07"))
                                            "+254${phoneNumber.substring(1)}"
                                        else phoneNumber
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:$formatted")
                                        }
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "Phone number not available", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error fetching user: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB518))
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Contact Client", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}
