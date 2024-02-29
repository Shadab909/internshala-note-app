package com.internshala.notes.data.repository

import android.app.Application
import android.content.ContentValues
import com.internshala.notes.common.Resource
import com.internshala.notes.data.db.DatabaseHelper
import com.internshala.notes.domain.model.Note
import com.internshala.notes.domain.repository.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class NotesRepositoryImpl(application: Application) : NotesRepository {
    private val dbHelper = DatabaseHelper(application)
    private val _allNotesFlow = MutableStateFlow<Resource<List<Note>>>(Resource.Loading)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshNotes()
        }
    }

    override suspend fun getAllNotes(): Flow<Resource<List<Note>>> = _allNotesFlow.asStateFlow()

    override suspend fun insert(note: Note): Flow<Resource<String>> = flow {
        Timber.d("Inserting note: $note")
        emit(Resource.Loading)
        try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_TITLE, note.title)
                put(DatabaseHelper.COLUMN_CONTENT, note.content)
                put(DatabaseHelper.COLUMN_TIMESTAMP, note.timestamp)
            }
            val rowId = db.insert(DatabaseHelper.TABLE_NAME, null, values)
            Timber.d("Note inserted at row: $rowId")
            db.close()
            emit(Resource.Success("Note added successfully."))
            refreshNotes()
        } catch (e: Exception) {
            Timber.e(e)
            emit(Resource.Error(e))
        }
    }

    override suspend fun update(note: Note): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_TITLE, note.title)
                put(DatabaseHelper.COLUMN_CONTENT, note.content)
                put(DatabaseHelper.COLUMN_TIMESTAMP, note.timestamp)
            }
            db.update(
                DatabaseHelper.TABLE_NAME,
                values,
                "${DatabaseHelper.COLUMN_ID} = ?",
                arrayOf(note.id.toString())
            )
            db.close()
            emit(Resource.Success("Note updated successfully."))
            refreshNotes()
        } catch (e: Exception) {
            Timber.e(e)
            emit(Resource.Error(e))
        }
    }

    override suspend fun delete(noteId : Long): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val db = dbHelper.writableDatabase
            db.delete(
                DatabaseHelper.TABLE_NAME,
                "${DatabaseHelper.COLUMN_ID} = ?",
                arrayOf(noteId.toString())
            )
            db.close()
            emit(Resource.Success("Note deleted successfully."))
            refreshNotes()
        } catch (e: Exception) {
            Timber.e(e)
            emit(Resource.Error(e))
        }
    }

    private suspend fun fetchAllNotes(): Flow<Resource<List<Note>>> = flow {
        emit(Resource.Loading)
        try {
            val notes = mutableListOf<Note>()
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME} ORDER BY ${DatabaseHelper.COLUMN_TIMESTAMP} DESC", null)
            Timber.d("Total notes found: ${cursor.count}")
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
                val titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)
                val contentIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT)
                val timestampIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP)

                // Check if the column index is valid
                val id = if (idIndex >= 0) cursor.getLong(idIndex) else 0
                val title = if (titleIndex >= 0) cursor.getString(titleIndex) else ""
                val content = if (contentIndex >= 0) cursor.getString(contentIndex) else ""
                val timestamp = if (timestampIndex >= 0) cursor.getLong(timestampIndex) else 0

                val note = Note(id, title, content,timestamp)
                notes.add(note)
            }
            cursor.close()
            db.close()
            emit(Resource.Success(notes))
        } catch (e: Exception) {
            Timber.e(e)
            emit(Resource.Error(e))
        }
    }

    private suspend fun refreshNotes() {
        fetchAllNotes().collect { resource ->
            if (resource is Resource.Success) {
                _allNotesFlow.value = resource
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: NotesRepositoryImpl? = null

        fun getInstance(application: Application): NotesRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                val instance = NotesRepositoryImpl(application)
                INSTANCE = instance
                instance
            }
        }
    }
}