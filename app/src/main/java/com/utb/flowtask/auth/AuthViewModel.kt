package com.utb.flowtask.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(
        if (auth.currentUser != null) AuthState.Authenticated else AuthState.Idle
    )
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _authState.value = if (task.isSuccessful) {
                    AuthState.Authenticated
                } else {
                    AuthState.Error(traducirError(task.exception))
                }
            }
    }

    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _authState.value = if (task.isSuccessful) {
                    AuthState.Authenticated
                } else {
                    AuthState.Error(traducirError(task.exception))
                }
            }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    private fun traducirError(exception: Exception?): String {
        if (exception is FirebaseNetworkException) {
            return "Sin conexión a internet. Verifica tu red e inténtalo de nuevo."
        }
        val codigo = (exception as? FirebaseAuthException)?.errorCode ?: ""
        return when (codigo) {
            "ERROR_INVALID_EMAIL"           -> "El formato del correo electrónico no es válido."
            "ERROR_WRONG_PASSWORD"          -> "Contraseña incorrecta. Inténtalo de nuevo."
            "ERROR_USER_NOT_FOUND"          -> "No existe una cuenta con este correo electrónico."
            "ERROR_USER_DISABLED"           -> "Esta cuenta ha sido deshabilitada."
            "ERROR_TOO_MANY_REQUESTS"       -> "Demasiados intentos fallidos. Intenta más tarde."
            "ERROR_EMAIL_ALREADY_IN_USE"    -> "Este correo electrónico ya está registrado."
            "ERROR_WEAK_PASSWORD"           -> "La contraseña debe tener al menos 6 caracteres."
            "ERROR_NETWORK_REQUEST_FAILED"  -> "Sin conexión a internet. Verifica tu red e inténtalo de nuevo."
            "ERROR_OPERATION_NOT_ALLOWED"   -> "Este método de inicio de sesión no está habilitado."
            "ERROR_INVALID_CREDENTIAL"      -> "Las credenciales son incorrectas o han expirado."
            else                            -> "Ocurrió un error inesperado. Inténtalo de nuevo."
        }
    }
}