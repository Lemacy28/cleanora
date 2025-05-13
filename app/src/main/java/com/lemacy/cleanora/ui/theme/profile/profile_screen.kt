package com.lemacy.cleanora.ui.theme.profile

import android.R.attr.name
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


import androidx.compose.ui.text.font.FontVariation.weight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lemacy.cleanora.data.AuthViewModel
import com.lemacy.cleanora.model.Cleaner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.lemacy.cleanora.model.User
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_HOME
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_JOB_SEARCH
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_PROFILE
import com.lemacy.cleanora.ui.theme.home.LemonGreen


@Composable
fun ProfileScreen(navController: NavHostController, viewModel: AuthViewModel = viewModel()) {
    val cleaner by viewModel.currentUser.collectAsState()
    var isDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUserData()
    }

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
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.Black)
                }
            }

            // Display loading or profile info
            if (cleaner != null) {
                ProfileCard(
                    name = cleaner?.name ?: "N/A",
                    age = cleaner?.age ?: "N/A",
                    skills = cleaner?.skills ?: "N/A",
                    location = cleaner?.location ?: "N/A",
                    phoneNumber = cleaner?.phoneNumber ?: "N/A"
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        // Bottom Navigation Bar
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

        if (isDialogOpen) {
            EditProfileDialog(
                isOpen = isDialogOpen,
                cleaner = cleaner,
                onDismiss = {
                    isDialogOpen = false
                    viewModel.resetProfileUpdateFlag()
                },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    isOpen: Boolean,
    cleaner: Cleaner?,
    onDismiss: () -> Unit,
    viewModel: AuthViewModel
) {
    var name by remember { mutableStateOf(cleaner?.name.orEmpty()) }
    var age by remember { mutableStateOf(cleaner?.age.orEmpty()) }
    var skills by remember { mutableStateOf(cleaner?.skills.orEmpty()) }
    var location by remember { mutableStateOf(cleaner?.location.orEmpty()) }
    var phoneNumber by remember { mutableStateOf(cleaner?.phoneNumber.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") })
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") })
                OutlinedTextField(
                    value = skills,
                    onValueChange = { skills = it },
                    label = { Text("Skills") })
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") })
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.updateProfile(name, age, skills, location, phoneNumber)
                onDismiss()
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
fun ProfileCard(name: String, age: String, skills: String, location: String, phoneNumber: String) {
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
            ProfileInfoItem("Age", age)
            ProfileInfoItem("Skills", skills)
            ProfileInfoItem("Location", location)
            ProfileInfoItem("Phone Number", phoneNumber)
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(
            text = value.ifEmpty { " " },
            fontSize = 16.sp,
            color = if (value.isEmpty()) Color.Gray else Color.Black
        )
    }
}