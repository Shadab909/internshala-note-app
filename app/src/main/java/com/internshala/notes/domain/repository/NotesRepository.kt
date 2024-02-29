package com.internshala.notes.domain.repository

import com.internshala.notes.common.Resource
import com.internshala.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun getAllNotes(): Flow<Resource<List<Note>>>
    suspend fun insert(note: Note) : Flow<Resource<String>>
    suspend fun update(note: Note) : Flow<Resource<String>>
    suspend fun delete(noteId : Long) : Flow<Resource<String>>
}