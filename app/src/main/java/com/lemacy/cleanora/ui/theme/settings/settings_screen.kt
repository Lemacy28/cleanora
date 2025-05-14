package com.lemacy.cleanora.ui.theme.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Create DataStore
val Context.dataStore by preferencesDataStore(name = "settings")

// Keys
val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
val LANGUAGE_KEY = stringPreferencesKey("language")

@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var notificationsEnabled by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }

    // Load saved values on launch
    LaunchedEffect(Unit) {
        val prefs = context.dataStore.data.first()
        notificationsEnabled = prefs[NOTIFICATIONS_KEY] ?: false
        darkModeEnabled = prefs[DARK_MODE_KEY] ?: false
        selectedLanguage = prefs[LANGUAGE_KEY] ?: "English"
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FFF3))
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Text("Settings", fontSize = 20.sp, modifier = Modifier.padding(start = 8.dp))
        }

        // Notification Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Enable Notifications", fontSize = 16.sp)
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = {
                    notificationsEnabled = it
                    scope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[NOTIFICATIONS_KEY] = it
                        }
                    }
                }
            )
        }

        // Dark Mode Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dark Mode", fontSize = 16.sp)
            Switch(
                checked = darkModeEnabled,
                onCheckedChange = {
                    darkModeEnabled = it
                    scope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[DARK_MODE_KEY] = it
                        }
                    }
                }
            )
        }

        // Language Switch
        Text("Select Language", fontSize = 16.sp, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LanguageButton("English", selectedLanguage == "English") {
                selectedLanguage = "English"
                scope.launch {
                    context.dataStore.edit { prefs ->
                        prefs[LANGUAGE_KEY] = "English"
                    }
                }
            }
            LanguageButton("Spanish", selectedLanguage == "Spanish") {
                selectedLanguage = "Spanish"
                scope.launch {
                    context.dataStore.edit { prefs ->
                        prefs[LANGUAGE_KEY] = "Spanish"
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageButton(language: String, isSelected: Boolean, onClick: () -> Unit) {
    val selectedColor = Color(0xFFB6EFA2) // lemon green
    val unselectedColor = MaterialTheme.colorScheme.surfaceVariant

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) selectedColor else unselectedColor,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .height(40.dp)
    ) {
        Text(text = language)
    }
}
