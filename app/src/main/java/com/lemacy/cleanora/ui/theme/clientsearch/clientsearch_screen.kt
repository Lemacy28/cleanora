package com.lemacy.cleanora.ui.theme.clientsearch

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemacy.cleanora.data.AuthViewModel
import com.lemacy.cleanora.data.CleanerSearchViewModel
import com.lemacy.cleanora.model.Cleaner
import com.lemacy.cleanora.model.Client
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_HOME
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_PROFILE
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_SEARCH
import com.lemacy.cleanora.navigation.NavRoutes.JOB_POST
import com.lemacy.cleanora.navigation.NavRoutes.MPESA_PAYMENT
import com.lemacy.cleanora.ui.theme.home.LemonGreen
@Composable
fun ClientCleanerSearchScreen(
    cleanerSearchViewModel: CleanerSearchViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    var searchQuery by remember { mutableStateOf("") }
    val cleaners by cleanerSearchViewModel.cleaners.collectAsState()
    val isLoading by cleanerSearchViewModel.isFetchingCleaners.collectAsState()
    val error by cleanerSearchViewModel.cleanerError.collectAsState()
    var selectedCleaner by remember { mutableStateOf<Cleaner?>(null) }
    val context = LocalContext.current
    val lightLemonGreen = Color(0xFFE4F4D7)

    LaunchedEffect(Unit) {
        cleanerSearchViewModel.fetchAllCleaners()
    }

    val filteredCleaners = cleaners.filter {
        it.skills.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightLemonGreen) // Apply the light lemon green background here
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp) // Leave space for the bottom bar
        ) {
            // Available Cleaners Text
            Text(
                text = "Available Cleaners",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by skill") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Loading, Error, or Cleaner List
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
                filteredCleaners.isEmpty() -> Text("No cleaners found.", modifier = Modifier.align(Alignment.CenterHorizontally))
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredCleaners) { cleaner ->
                        CleanerCard(cleaner) { selectedCleaner = cleaner }
                    }
                }
            }
        }

        // ✅ Bottom Navigation (Fixed to Bottom)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(LemonGreen)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.navigate(CLIENT_HOME) {
                    popUpTo(CLIENT_HOME) { inclusive = true }
                }
            }) {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.Black)
            }
            IconButton(onClick = { navController.navigate(JOB_POST) }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Post Job", tint = Color.Black)
            }
            IconButton(onClick = { navController.navigate(CLIENT_SEARCH) }) {
                Icon(Icons.Default.Search, contentDescription = "Search Cleaners", tint = Color.Black)
            }
            IconButton(onClick = { navController.navigate(MPESA_PAYMENT) }) {
                Icon(Icons.Default.Payments, contentDescription = "Pay Cleaners", tint = Color.Black)
            }
            IconButton(onClick = { navController.navigate(CLIENT_PROFILE) }) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.Black)
            }
        }
    }

    // Cleaner Detail Dialog
    selectedCleaner?.let { cleaner ->
        CleanerDetailDialog(
            context = context,
            cleaner = cleaner,
            onClose = { selectedCleaner = null },
            onAccept = {
                val client = authViewModel.currentUser.value
                val clientId = when (client) {
                    is Client -> client.id
                    is Cleaner -> client.id
                    else -> null
                }
                clientId?.let { id ->
                    cleanerSearchViewModel.acceptCleaner(id, cleaner) { success ->
                        if (success) {
                            Toast.makeText(context, "Cleaner accepted!", Toast.LENGTH_SHORT).show()
                            selectedCleaner = null
                        } else {
                            Toast.makeText(context, "Failed to accept cleaner.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: run {
                    Toast.makeText(context, "User is not authenticated.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
fun CleanerCard(cleaner: Cleaner, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6FFF0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(cleaner.name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text("Email: ${cleaner.email}", fontSize = 14.sp)
            Text("Skills: ${cleaner.skills}", fontSize = 14.sp)
            Text("Location: ${cleaner.location}", fontSize = 14.sp)
        }
    }
}



@Composable
fun CleanerDetailDialog(
    context: Context,
    cleaner: Cleaner,
    onClose: () -> Unit,
    onAccept: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    AlertDialog(
        onDismissRequest = { onClose() },
        title = { Text(text = cleaner.name, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Email: ${cleaner.email}")
                Text("Skills: ${cleaner.skills}")
                Text("Location: ${cleaner.location}")
                Text("Phone: ${cleaner.phoneNumber}")
            }
        },
        confirmButton = {
            TextButton(onClick = onAccept,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB518))) {
                Text("Accept Cleaner")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = {
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
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onClose) {
                    Text("Close")
                }
            }
        }
    )
}
