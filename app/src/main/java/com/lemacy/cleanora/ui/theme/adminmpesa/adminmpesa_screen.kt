package com.lemacy.cleanora.ui.theme.adminmpesa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.lemacy.cleanora.data.MpesaAdminViewModel
import com.lemacy.cleanora.navigation.NavRoutes.ADMIN_DASHBOARD
import com.lemacy.cleanora.navigation.NavRoutes.ADMIN_MPESA
import com.lemacy.cleanora.ui.theme.admin.BottomNavigationBar
import kotlinx.coroutines.delay

@Composable
fun AdminMpesaScreen(
    navController: NavHostController,
    viewModel: MpesaAdminViewModel = viewModel()
) {
    var phoneNumber by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var paymentMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FFF2)) // light lemon green
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pay Cleaner via M-PESA",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF6CBF47)
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Cleaner Phone (e.g., 2547XXXXXXX)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (KES)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    if (phoneNumber.isNotBlank() && amount.isNotBlank()) {
                        isSending = true
                        paymentMessage = "Sending Payment..."
                        viewModel.sendMoneyToCleaner(phoneNumber, amount)

                        paymentMessage = "Payment request sent successfully."
                        isSending = false
                    } else {
                        paymentMessage = "Please fill in both phone and amount."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6CBF47))
            ) {
                Text(text = if (isSending) "Sending..." else "Send Payment")
            }

            if (paymentMessage.isNotEmpty()) {
                Text(
                    text = paymentMessage,
                    color = Color(0xFF6CBF47),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Bottom Navigation (your style)
        BottomNavigationBa(navController = navController)
    }
}
@Composable
fun BottomNavigationBa(navController: NavController, modifier: Modifier = Modifier) {
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
                    popUpTo(ADMIN_DASHBOARD)
                }
            }) {
                Icon(Icons.Default.AttachMoney, contentDescription = "Pay", tint = Color.White)
            }
        }
    }
}
