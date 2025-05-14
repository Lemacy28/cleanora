package com.lemacy.cleanora.ui.theme.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lemacy.cleanora.data.AuthViewModel
import com.lemacy.cleanora.navigation.NavRoutes.ADMIN_DASHBOARD
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_HOME
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_HOME
import com.lemacy.cleanora.navigation.NavRoutes.LOGIN
import com.lemacy.cleanora.navigation.NavRoutes.REGISTER
import com.lemacy.cleanora.ui.theme.home.LemonGreen


@Composable
fun LoginScreen(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6FFF0))
    ) {
        // Top Curved Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(LemonGreen, Color(0xFFA9E34B))
                    ),
                    shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Cleanora",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        // Login Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 200.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Welcome Back", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LemonGreen)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LemonGreen,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LemonGreen,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isLoading = true
                        loginError = null

                        // ✅ Hardcoded admin check before Firebase login
                        if (email == "admin@cleanora.com" && password == "admin123") {
                            isLoading = false
                            navController.navigate(ADMIN_DASHBOARD) {
                                popUpTo(LOGIN) { inclusive = true }
                            }
                        } else {
                            authViewModel.loginUser(email, password) { role ->
                                isLoading = false
                                if (!role.isNullOrBlank()) {
                                    val destination = when (role.lowercase()) {
                                        "client" -> "client_home"
                                        "cleaner" -> "cleaner_home"
                                        else -> null
                                    }
                                    destination?.let {
                                        navController.navigate(it) {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } ?: run {
                                        loginError = "Unknown user role."
                                    }
                                } else {
                                    loginError = "Login failed. Please try again."
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LemonGreen)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text("Login", fontSize = 16.sp, color = Color.Black)
                    }
                }


                loginError?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = it, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = {
                    navController.navigate(REGISTER)
                }) {
                    Text("Don't have an account? Register", color = LemonGreen)
                }
            }
        }
    }
}


@Preview
@Composable
private fun LoginPreview() {
    LoginScreen(rememberNavController())

}
