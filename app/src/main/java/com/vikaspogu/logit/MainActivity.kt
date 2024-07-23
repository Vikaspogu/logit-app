package com.vikaspogu.logit

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.vikaspogu.logit.ui.LogItApp
import com.vikaspogu.logit.ui.theme.LogItTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LogItTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    LogItApp(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
