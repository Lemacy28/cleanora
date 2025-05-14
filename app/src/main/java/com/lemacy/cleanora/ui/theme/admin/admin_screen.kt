package com.lemacy.cleanora.ui.theme.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lemacy.cleanora.data.AdminViewModel

// AdminDashboardScreen.kt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.lemacy.cleanora.model.Job
import com.lemacy.cleanora.model.User
import com.lemacy.cleanora.navigation.NavRoutes.ADMIN_DASHBOARD
import com.lemacy.cleanora.navigation.NavRoutes.ADMIN_MPESA
import com.lemacy.cleanora.navigation.NavRoutes.GENERAL_HOME

@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminViewModel = viewModel()
) {
    val jobs by viewModel.jobs.collectAsState()
    val users by viewModel.users.collectAsState()
    val jobCount by viewModel.jobCount.collectAsState()
    val userCount by viewModel.userCount.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FFF2))
                .padding(bottom = 56.dp) // Space for bottom bar
        ) {
            // Top bar with dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Admin Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                )

                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(GENERAL_HOME) {
                                popUpTo(0)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DashboardStatCard("Total Users", userCount.toString())
                DashboardStatCard("Total Jobs", jobCount.toString())
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Recent Jobs",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
            LazyColumn(modifier = Modifier.height(200.dp)) {
                items(jobs.take(5)) { job ->
                    JobCard(job)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Registered Users",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
            LazyColumn(modifier = Modifier.height(200.dp)) {
                items(users.take(5)) { user ->
                    UserCard(user)
                }
            }
        }

        BottomNavigationBar(navController, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun DashboardStatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(width = 150.dp, height = 80.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6CBF47), // Lemon green
            contentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun JobCard(job: Job) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Title: ${job.title}", fontWeight = FontWeight.SemiBold)
            Text("Client ID: ${job.clientId}")
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Name: ${user.name}", fontWeight = FontWeight.SemiBold)
            Text("Phone: ${user.phoneNumber}")
            Text("Location: ${user.location}")
            Text("Role: ${user.role}")
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    BottomAppBar(
        modifier = modifier,
        containerColor = Color(0xFF6CBF47),
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {
                navController.navigate(ADMIN_DASHBOARD) {
                    popUpTo(ADMIN_DASHBOARD) { inclusive = true }
                }
            }) {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
            }

            IconButton(onClick = {
                navController.navigate(ADMIN_MPESA) {
                    popUpTo("adminDashboard")
                }
            }) {
                Icon(Icons.Default.AttachMoney, contentDescription = "Pay", tint = Color.White)
            }
        }
    }
}
