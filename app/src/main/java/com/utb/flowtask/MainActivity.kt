package com.utb.flowtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.utb.flowtask.ui.screens.FormScreen
import com.utb.flowtask.ui.screens.MainScreen
import com.utb.flowtask.ui.theme.FlowTaskTheme
import com.utb.flowtask.viewmodel.TaskViewModel

// Definición de las pantallas disponibles
enum class Screen {
    MAIN, FORM
}

class MainActivity : ComponentActivity() {
    // Inicialización del ViewModel que sobrevive a cambios de configuración
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowTaskTheme {
                // Estado de navegación guardable para mantener la pantalla activa al rotar el dispositivo
                var currentScreen by rememberSaveable { mutableStateOf(Screen.MAIN) }

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (currentScreen) {
                        Screen.MAIN -> {
                            MainScreen(
                                onNavigateToForm = { currentScreen = Screen.FORM },
                                viewModel = viewModel
                            )
                        }
                        Screen.FORM -> {
                            FormScreen(
                                onNavigateToMain = { currentScreen = Screen.MAIN },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
