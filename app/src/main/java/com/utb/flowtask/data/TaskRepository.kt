package com.utb.flowtask.data

import android.content.ContentValues
import android.content.Context
import com.utb.flowtask.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(context: Context) {

    private val dbHelper = TaskDbHelper(context)

    suspend fun getAll(): List<Task> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TaskDbHelper.TABLE_TASKS, null, null, null, null, null,
            "${TaskDbHelper.COL_IS_COMPLETED} ASC, ${TaskDbHelper.COL_TITLE} ASC"
        )
        val tasks = mutableListOf<Task>()
        cursor.use {
            while (it.moveToNext()) {
                tasks.add(
                    Task(
                        id = it.getString(it.getColumnIndexOrThrow(TaskDbHelper.COL_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(TaskDbHelper.COL_TITLE)),
                        description = it.getString(it.getColumnIndexOrThrow(TaskDbHelper.COL_DESCRIPTION)) ?: "",
                        isCompleted = it.getInt(it.getColumnIndexOrThrow(TaskDbHelper.COL_IS_COMPLETED)) == 1
                    )
                )
            }
        }
        tasks
    }

    suspend fun insert(task: Task) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TaskDbHelper.COL_ID, task.id)
            put(TaskDbHelper.COL_TITLE, task.title)
            put(TaskDbHelper.COL_DESCRIPTION, task.description)
            put(TaskDbHelper.COL_IS_COMPLETED, if (task.isCompleted) 1 else 0)
        }
        db.insert(TaskDbHelper.TABLE_TASKS, null, values)
    }

    suspend fun update(task: Task) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TaskDbHelper.COL_TITLE, task.title)
            put(TaskDbHelper.COL_DESCRIPTION, task.description)
            put(TaskDbHelper.COL_IS_COMPLETED, if (task.isCompleted) 1 else 0)
        }
        db.update(
            TaskDbHelper.TABLE_TASKS,
            values,
            "${TaskDbHelper.COL_ID} = ?",
            arrayOf(task.id)
        )
    }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete(TaskDbHelper.TABLE_TASKS, "${TaskDbHelper.COL_ID} = ?", arrayOf(id))
    }
}