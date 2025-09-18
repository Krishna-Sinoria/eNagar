package com.example.enagar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.enagar.presentation.navigation.NavGraph
import com.example.enagar.presentation.viewModel.CitizenViewModel
import com.example.enagar.ui.theme.ENagarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel : CitizenViewModel = hiltViewModel()
            ENagarTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavGraph()

                }
            }
        }
    }
}

