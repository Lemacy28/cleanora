package com.lemacy.cleanora.ui.theme.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lemacy.cleanora.data.JobViewModel
import com.lemacy.cleanora.model.Job
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_HOME
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_JOB_SEARCH
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_PROFILE
import com.lemacy.cleanora.navigation.NavRoutes.GENERAL_HOME
import kotlinx.coroutines.flow.StateFlow


// Lemon green palette
val LemonGreen = Color(0xFFA8E10C)
val LightGreen = Color(0xFFDDF8AA)
val White = Color(0xFFFFFFFF)

@Composable
fun CleanerHomeScreen(navController: NavHostController) {
    val jobViewModel: JobViewModel = viewModel()
    val jobs by jobViewModel.jobs.collectAsState()
    val acceptedJobs by jobViewModel.acceptedJobs.collectAsState()
    val isLoading by jobViewModel.isLoading.collectAsState()
    val error by jobViewModel.error.collectAsState()

    var menuExpanded by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        jobViewModel.fetchAvailableJobs()
        jobViewModel.fetchAcceptedJobs()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGreen)
    ) {
        // Main Content Column (scrollable content with top bar)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp) // reserve space for bottom bar
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LemonGreen)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cleanora", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Black)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
//                        DropdownMenuItem(
//                            text = {
//                                Row(verticalAlignment = Alignment.CenterVertically) {
//                                    Icon(Icons.Default.Settings, null, tint = Color.Black)
//                                    Spacer(Modifier.width(8.dp))
//                                    Text("Settings")
//                                }
//                            },
//                            onClick = {navController.navigate(SETTINGS)
//                                showSettingsDialog = true
//                                menuExpanded = false
//                            }
//                        )
//                        DropdownMenuItem(
//                            text = {
//                                Row(verticalAlignment = Alignment.CenterVertically) {
//                                    Icon(Icons.Default.Help, null, tint = Color.Black)
//                                    Spacer(Modifier.width(8.dp))
//                                    Text("Help & Support")
//                                }
//                            },
//                            onClick = {
//                                showHelpDialog = true
//                                menuExpanded = false
//                            }
//                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ExitToApp, null, tint = Color.Black)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Logout")
                                }
                            },
                            onClick = {
                                navController.navigate(GENERAL_HOME) {
                                    popUpTo(CLEANER_HOME) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }

            // Main content
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Welcome Back!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                if (acceptedJobs.isNotEmpty()) {
                    Text("Accepted Jobs", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn {
                        items(acceptedJobs) { job ->
                            AcceptedJobCard(job = job, onRemove = { jobViewModel.removeAcceptedJob(job.id) })
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Optional job listing block
//                if (isLoading) {
//                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
//                } else if (error != null) {
//                    Text("Error: $error", color = Color.Red)
//                } else {
//                    LazyColumn {
//                        items(jobs) { job ->
//                            JobCard(job = job) {
//                                navController.navigate("job_details/${job.id}")
//                            }
//                        }
//                    }
//                }
            }
        }

        // Bottom Navigation Bar (always fixed at bottom)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(LemonGreen)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
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


        // Settings Dialog
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("• Change Theme", modifier = Modifier.clickable { /* Handle theme change */ }.padding(4.dp))
                        Text("• Notification Preferences", modifier = Modifier.clickable { /* Handle notification */ }.padding(4.dp))
                        Text("• Account Settings", modifier = Modifier.clickable { navController.navigate(CLEANER_PROFILE) }.padding(4.dp))
                        Text("• Privacy Policy", modifier = Modifier.clickable { /* Open privacy link */ }.padding(4.dp))
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
                        Text("• FAQs", modifier = Modifier.clickable { /* Open FAQ */ }.padding(4.dp))
                        Text("• Contact Support", modifier = Modifier.clickable { /* Open support email or screen */ }.padding(4.dp))
                        Text("• Report a Problem", modifier = Modifier.clickable { /* Open report screen */ }.padding(4.dp))
                        Text("• Terms & Conditions", modifier = Modifier.clickable { /* Open terms */ }.padding(4.dp))
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }


// --- Dialogs ---

@Composable
fun SettingsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("• Notification preferences")
                Text("• Theme customization")
                Text("• Language settings")
                Text("• App version: 1.0.0")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("• FAQs")
                Text("• Contact support at: support@cleanora.com")
                Text("• Tips and resources for cleaners")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// --- Job Cards ---

@Composable
fun JobCard(job: Job, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(job.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(job.description, fontSize = 14.sp)
            Text(" ${job.location}", fontSize = 12.sp)
            Text(" ${job.price}", fontSize = 12.sp)
        }
    }
}

@Composable
fun AcceptedJobCard(job: Job, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(job.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(job.description, fontSize = 14.sp)
            Text(" ${job.location}", fontSize = 12.sp)
            Text(" ${job.price}", fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRemove,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Remove", color = Color.White)
            }
        }
    }
}
