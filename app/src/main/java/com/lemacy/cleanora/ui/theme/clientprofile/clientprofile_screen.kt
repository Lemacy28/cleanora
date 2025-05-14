package com.lemacy.cleanora.ui.theme.clientprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.lemacy.cleanora.data.AuthViewModel
import com.lemacy.cleanora.model.Client
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_HOME
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_HOME
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_PROFILE
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_SEARCH
import com.lemacy.cleanora.navigation.NavRoutes.JOB_POST
import com.lemacy.cleanora.navigation.NavRoutes.MPESA_PAYMENT
import com.lemacy.cleanora.ui.theme.home.LemonGreen
import com.lemacy.cleanora.ui.theme.profile.ProfileCard
import com.lemacy.cleanora.ui.theme.profile.ProfileInfoItem
@Composable
fun ClientProfileScreen(navController: NavHostController, viewModel: AuthViewModel = viewModel()) {
    val client by viewModel.currentUser.collectAsState()
    var isDialogOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FFF3))
                .padding(bottom = 56.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB6EFA2))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("My Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                IconButton(onClick = { isDialogOpen = true }) {
                    androidx.compose.material3.Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.Black
                    )
                }
            }

            if (client != null) {
                ProfileCardClient(
                    name = client?.name ?: "N/A",
                    location = client?.location ?: "N/A",
                    phoneNumber = client?.phoneNumber ?: "N/A"
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        // Bottom Navigation Bar
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {}

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LemonGreen)
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        navController.navigate(CLIENT_HOME) {
                            popUpTo(CLIENT_HOME) { inclusive = true }
                        }
                    }) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = { navController.navigate(JOB_POST) }) {
                        androidx.compose.material3.Icon(
                            Icons.Default.AddCircle,
                            contentDescription = "Post Job",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = { navController.navigate(CLIENT_SEARCH) }) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Search,
                            contentDescription = "Search Cleaners",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = { navController.navigate(MPESA_PAYMENT) }) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Payments,
                            contentDescription = "Pay Cleaners",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = { navController.navigate(CLIENT_PROFILE) }) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        if (isDialogOpen) {
            EditClientProfileDialog(
                client = client as? Client,
                onDismiss = { isDialogOpen = false },
                onSave = { name, location, phoneNumber ->
                    viewModel.updateClientProfile(name, location, phoneNumber)
                    isDialogOpen = false // Close immediately after saving
                }
            )
        }
    }
}

@Composable
fun EditClientProfileDialog(
    client: Client?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(client?.name.orEmpty()) }
    var location by remember { mutableStateOf(client?.location.orEmpty()) }
    var phoneNumber by remember { mutableStateOf(client?.phoneNumber.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") }
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(name, location, phoneNumber)
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = Color.White
    )
}

@Composable
fun ProfileCardClient(name: String, location: String, phoneNumber: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileInfoItem("Name", name)
            ProfileInfoItem("Location", location)
            ProfileInfoItem("Phone Number", phoneNumber)
        }
    }
}
