package com.utb.flowtask.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utb.flowtask.data.TaskRepository
import com.utb.flowtask.model.Task
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(application)
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.clear()
            _tasks.addAll(repository.getAll())
        }
    }

    fun addTask(title: String, description: String) {
        if (title.isNotBlank()) {
            val task = Task(title = title.trim(), description = description.trim())
            viewModelScope.launch {
                repository.insert(task)
                _tasks.add(task)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task.id)
            _tasks.remove(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        val index = _tasks.indexOf(task)
        if (index != -1) {
            val updated = task.copy(isCompleted = !task.isCompleted)
            viewModelScope.launch {
                repository.update(updated)
                _tasks[index] = updated
            }
        }
    }
}