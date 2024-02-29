package com.internshala.notes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internshala.notes.common.Resource
import com.internshala.notes.domain.model.Note
import com.internshala.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class NotesViewModel(private val noteRepository: NotesRepository) : ViewModel() {
    fun getAllNotes(): Flow<Resource<List<Note>>> = flow {
        viewModelScope.launch {
            noteRepository.getAllNotes()
        }
    }

    fun insert(note: Note): Flow<Resource<String>> = flow {
        viewModelScope.launch {
            noteRepository.insert(note)
        }
    }

    fun update(note: Note): Flow<Resource<String>> = flow {
        viewModelScope.launch {
            noteRepository.update(note)
        }
    }

    fun delete(noteId: Long): Flow<Resource<String>> = flow {
        viewModelScope.launch {
            noteRepository.delete(noteId)
        }
    }
}