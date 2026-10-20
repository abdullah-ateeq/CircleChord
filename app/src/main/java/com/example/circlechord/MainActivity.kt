package com.example.circlechord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.circlechord.ui.CircleChordViewModel
import com.example.circlechord.ui.navigation.CircleChordApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(
            this,
            CircleChordViewModel.provideFactory(application)
        )[CircleChordViewModel::class.java]

        setContent {
            CircleChordApp(viewModel = viewModel)
        }
    }
}
