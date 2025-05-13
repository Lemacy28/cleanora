package com.lemacy.cleanora.ui.theme.mpesa

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.lemacy.cleanora.data.MpesaViewModel
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_HOME
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_PROFILE
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_SEARCH
import com.lemacy.cleanora.navigation.NavRoutes.JOB_POST
import com.lemacy.cleanora.navigation.NavRoutes.MPESA_PAYMENT
import com.lemacy.cleanora.ui.theme.home.LemonGreen
import com.lemacy.cleanora.ui.theme.jobpostdialog.BottomNavIcon

@Composable
fun MpesaPaymentScreen(
    navController: NavHostController,
    viewModel: MpesaViewModel = viewModel()
) {
    val context = LocalContext.current
    var phone by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val lightLemonGreen = Color(0xFFE4F4D7)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightLemonGreen) // Apply the light lemon green background here
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp, start = 24.dp, end = 24.dp, top = 24.dp), // Leave space for bottom nav
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Pay Service",
                fontSize = 26.sp,
                color = Color(0xFF7BBF3F),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Enter your Phone.no (2547XXXXXXXX)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (phone.isBlank() || amount.isBlank()) {
                        Toast.makeText(context, "Fill both fields", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        viewModel.initiatePayment(phone, amount) { success ->
                            isLoading = false
                            if (success) {
                                Toast.makeText(context, "Payment initiated", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Payment failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7BBF3F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Pay Service", color = Color.White)
                }
            }
        }

        // Bottom Navigation Bar
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
