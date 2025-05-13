package com.lemacy.cleanora.ui.theme.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lemacy.cleanora.model.Job
import com.lemacy.cleanora.navigation.NavRoutes.LOGIN
import com.lemacy.cleanora.navigation.NavRoutes.REGISTER
import com.lemacy.cleanora.R
import com.lemacy.cleanora.data.JobViewModel


@Composable
fun GeneralHomeScreen(navController: NavHostController) {
    val jobViewModel: JobViewModel = viewModel()
    val jobList by jobViewModel.jobs.collectAsState()

    LaunchedEffect(Unit) {
        jobViewModel.fetchAvailableJobs()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF1FFE7), Color(0xFFE7FCD3)) // lemon green gradient
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Welcome to",
                fontSize = 20.sp,
                color = Color.DarkGray
            )
            Text(
                "Cleanora",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF7CB518)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate(LOGIN) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB518))
                ) {
                    Icon(Icons.Default.Login, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Login", color = Color.White)
                }

                OutlinedButton(
                    onClick = { navController.navigate(REGISTER) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(30),
                    border = BorderStroke(1.5.dp, Color(0xFF7CB518))
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF7CB518))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Register", color = Color(0xFF7CB518))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Featured Jobs",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4D4D4D),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (jobList.isEmpty()) {
                        Text(
                            "No jobs available at the moment.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(jobList) { job ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FFF5))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            job.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF4D4D4D)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            job.description,
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            "Location: ${job.location}",
                                            fontSize = 13.sp,
                                            color = Color(0xFF6C6C6C)
                                        )
                                        Text(
                                            "Price: $${job.price}",
                                            fontSize = 13.sp,
                                            color = Color(0xFF6C6C6C)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { navController.navigate(LOGIN) },
                                            shape = RoundedCornerShape(20),
                                            modifier = Modifier.align(Alignment.End),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB518))
                                        ) {
                                            Icon(Icons.Default.Login, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Accept Job", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.navigate(LOGIN) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(30),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB518))
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Post a Job (Login Required)", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview
@Composable
private fun Homepreview() {
    GeneralHomeScreen(rememberNavController())
}
