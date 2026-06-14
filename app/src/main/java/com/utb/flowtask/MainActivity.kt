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
import com.utb.flowtask.auth.AuthState
import com.utb.flowtask.auth.AuthViewModel
import com.utb.flowtask.ui.screens.FormScreen
import com.utb.flowtask.ui.screens.LoginScreen
import com.utb.flowtask.ui.screens.MainScreen
import com.utb.flowtask.ui.screens.MapScreen
import com.utb.flowtask.ui.theme.FlowTaskTheme
import com.utb.flowtask.viewmodel.TaskViewModel

enum class Screen {
    LOGIN, MAIN, FORM, MAP
}

class MainActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowTaskTheme {
                val authState by authViewModel.authState.collectAsState()
                var currentScreen by rememberSaveable { mutableStateOf(Screen.LOGIN) }

                // Cuando el estado de auth cambia, redirigir automáticamente
                LaunchedEffect(authState) {
                    when (authState) {
                        is AuthState.Authenticated -> {
                            if (currentScreen == Screen.LOGIN) currentScreen = Screen.MAIN
                        }
                        is AuthState.Idle -> currentScreen = Screen.LOGIN
                        else -> Unit
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        Screen.LOGIN -> LoginScreen(authViewModel = authViewModel)
                        Screen.MAIN -> MainScreen(
                            onNavigateToForm = { currentScreen = Screen.FORM },
                            onNavigateToMap = { currentScreen = Screen.MAP },
                            onLogout = { authViewModel.logout() },
                            viewModel = taskViewModel
                        )
                        Screen.FORM -> FormScreen(
                            onNavigateToMain = { currentScreen = Screen.MAIN },
                            viewModel = taskViewModel
                        )
                        Screen.MAP -> MapScreen(
                            onNavigateBack = { currentScreen = Screen.MAIN }
                        )
                    }
                }
            }
        }
    }
}