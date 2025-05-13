package com.lemacy.cleanora.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.lemacy.cleanora.data.AuthViewModel
import com.lemacy.cleanora.data.JobViewModel
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_HOME
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_PROFILE
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_SEARCH
import com.lemacy.cleanora.navigation.NavRoutes.GENERAL_HOME
import com.lemacy.cleanora.navigation.NavRoutes.JOB_POST
import com.lemacy.cleanora.navigation.NavRoutes.MPESA_PAYMENT


@Composable
fun ClientHomeScreen(navController: NavHostController) {
    val jobViewModel: JobViewModel = viewModel()  // Use the JobViewModel to load jobs
    val clientId = Firebase.auth.currentUser?.uid  // Get current user ID (clientId)

    // Observe the clientJobs state
    val clientJobs by jobViewModel.clientJobs.collectAsState()

    var menuExpanded by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showJobPostDialog by remember { mutableStateOf(false) }

    // Load jobs for the current client when the screen is first launched
    LaunchedEffect(clientId) {
        clientId?.let {
            jobViewModel.loadJobsForClient(it)  // Load jobs for client
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(LightGreen)) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LemonGreen)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cleanora (Client)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Black)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black)
                                Spacer(Modifier.width(8.dp))
                                Text("Settings")
                            }
                        },
                        onClick = {
                            showSettingsDialog = true
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Help, contentDescription = null, tint = Color.Black)
                                Spacer(Modifier.width(8.dp))
                                Text("Help & Support")
                            }
                        },
                        onClick = {
                            showHelpDialog = true
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Black)
                                Spacer(Modifier.width(8.dp))
                                Text("Logout")
                            }
                        },
                        onClick = {
                            navController.navigate(GENERAL_HOME) {
                                popUpTo(CLIENT_HOME) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        // Main Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Your Posted Jobs",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (clientJobs.isEmpty()) {
                Text("You haven't posted any jobs yet.", color = Color.Gray)
            } else {
                clientJobs.forEach { job ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Title: ${job.title}", fontWeight = FontWeight.SemiBold)
                            Text("Description: ${job.description}")
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    job.id.let { jobViewModel.deleteJob(it) }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Remove", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar
        Row(
            modifier = Modifier
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
            IconButton(onClick = { navController.navigate(JOB_POST ) }) {
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

        // Settings Dialog
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("• Notification Settings", modifier = Modifier.clickable { }.padding(4.dp))
                        Text("• Change Theme", modifier = Modifier.clickable { }.padding(4.dp))
                        Text("• Account Settings", modifier = Modifier.clickable {
                            navController.navigate(CLIENT_PROFILE)
                        }.padding(4.dp))
                        Text("• Privacy Policy", modifier = Modifier.clickable { }.padding(4.dp))
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettingsDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Help Dialog
        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("• FAQs", modifier = Modifier.clickable { }.padding(4.dp))
                        Text("• Contact Support", modifier = Modifier.clickable { }.padding(4.dp))
                        Text("• Report a Problem", modifier = Modifier.clickable { }.padding(4.dp))
                        Text("• Terms & Conditions", modifier = Modifier.clickable { }.padding(4.dp))
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Job Post Dialog

    }
}
