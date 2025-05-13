package com.lemacy.cleanora.ui.theme.jobpostdialog

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.lemacy.cleanora.data.JobViewModel
import com.lemacy.cleanora.model.Client
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_HOME
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_PROFILE
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_SEARCH
import com.lemacy.cleanora.navigation.NavRoutes.GENERAL_HOME
import com.lemacy.cleanora.navigation.NavRoutes.JOB_POST
import com.lemacy.cleanora.navigation.NavRoutes.MPESA_PAYMENT
import com.lemacy.cleanora.ui.theme.home.LemonGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobPostScreen(navController: NavHostController) {
    val jobViewModel: JobViewModel = viewModel()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var showPriceError by remember { mutableStateOf(false) }

    val isLoading by jobViewModel.isLoading.collectAsState()
    val error by jobViewModel.error.collectAsState()

    val user = FirebaseAuth.getInstance().currentUser
    val client = Client(
        name = user?.displayName ?: "Unknown",
        phoneNumber = user?.phoneNumber ?: "Not Provided"
    )

    // Define the light lemon green color (lighter shade of the main lemon green)
    val lightLemonGreen = Color(0xFFE4F4D7) // Example: a very light lemon green

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightLemonGreen) // Apply the light lemon green background here
    ) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Add space for the bottom nav
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Full-Width Top Navigation Bar with the "Post a Cleaning Job" title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LemonGreen)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Post a Cleaning Job ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // Set the card background color to white
                ),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Job Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LemonGreen,
                            focusedLabelColor = LemonGreen
                        )
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LemonGreen,
                            focusedLabelColor = LemonGreen
                        )
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LemonGreen,
                            focusedLabelColor = LemonGreen
                        )
                    )

                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LemonGreen,
                            focusedLabelColor = LemonGreen
                        )
                    )

                    if (showPriceError) {
                        Text(
                            text = "Please enter a valid price",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val price = priceInput.toDoubleOrNull()
                            if (price != null) {
                                jobViewModel.postJob(title, description, location, price)
                                navController.popBackStack()
                            } else {
                                showPriceError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LemonGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Post Job", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = LemonGreen,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 8.dp)
                        )
                    }

                    error?.let {
                        Text(
                            text = "Error: $it",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        // ✅ Custom Bottom Bar (Material 3)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LemonGreen)
                .padding(vertical = 8.dp)
                .align(Alignment.BottomCenter), // Position at the bottom of the screen
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavIcon(icon = Icons.Default.Home, contentDesc = "Home") {
                navController.navigate(CLIENT_HOME) {
                    popUpTo(CLIENT_HOME) { inclusive = true }
                }
            }
            BottomNavIcon(icon = Icons.Default.AddCircle, contentDesc = "Post Job") {
                navController.navigate(JOB_POST)
            }
            BottomNavIcon(icon = Icons.Default.Search, contentDesc = "Search Cleaners") {
                navController.navigate(CLIENT_SEARCH)
            }
            BottomNavIcon(icon = Icons.Default.Payments, contentDesc = "Pay Cleaners") {
                navController.navigate(MPESA_PAYMENT)
            }
            BottomNavIcon(icon = Icons.Default.Person, contentDesc = "Profile") {
                navController.navigate(CLIENT_PROFILE)
            }
        }
    }
}

@Composable
fun BottomNavIcon(icon: ImageVector, contentDesc: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            icon,
            contentDescription = contentDesc,
            tint = Color.Black,
            modifier = Modifier.size(28.dp)
        )
    }
}
