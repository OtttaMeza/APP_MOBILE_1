package com.utb.flowtask.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.utb.flowtask.model.Task

class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    init {
        // Inicializamos con tareas de ejemplo para una mejor experiencia visual desde el inicio
        _tasks.add(
            Task(
                title = "Diseñar Mockups y Wireframes",
                description = "Crear los esquemas de distribución y el diseño visual de alta fidelidad.",
                isCompleted = true
            )
        )
        _tasks.add(
            Task(
                title = "Configurar Navegación en Jetpack Compose",
                description = "Implementar la transición de pantallas utilizando estado hoisted en MainActivity.",
                isCompleted = false
            )
        )
        _tasks.add(
            Task(
                title = "Asegurar Contraste y Accesibilidad",
                description = "Verificar objetivos táctiles de 48dp y contrastes de color según WCAG.",
                isCompleted = false
            )
        )
    }

    fun addTask(title: String, description: String) {
        if (title.isNotBlank()) {
            _tasks.add(Task(title = title.trim(), description = description.trim()))
        }
    }

    fun deleteTask(task: Task) {
        _tasks.remove(task)
    }

    fun toggleTaskCompletion(task: Task) {
        val index = _tasks.indexOf(task)
        if (index != -1) {
            _tasks[index] = task.copy(isCompleted = !task.isCompleted)
        }
    }
}
