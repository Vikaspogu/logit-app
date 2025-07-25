package com.vikaspogu.logit

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.vikaspogu.logit.data.repository.UserPreferencesRepository
import com.vikaspogu.logit.ui.LogItApp
import com.vikaspogu.logit.ui.theme.LogItTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if (isSystemInDarkTheme()){
                runBlocking { userPreferencesRepository.saveThemePreferences(true) }
            }
            val loggedInUser = runBlocking { userPreferencesRepository.username.first() }
            LogItTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    LogItApp(modifier = Modifier.fillMaxSize(), username = loggedInUser)
                }
            }
        }
    }
}
