package com.internshala.notes.presentation.screen.notes_list_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internshala.notes.common.Resource
import com.internshala.notes.domain.model.Note
import com.internshala.notes.domain.repository.NotesRepository
import com.internshala.notes.domain.repository.SharedPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class NoteListViewModel(
    private val noteRepository: NotesRepository,
    private val sharedPreferenceRepository: SharedPreferenceRepository
) : ViewModel() {
    suspend fun getAllNotes(): Flow<Resource<List<Note>>> = noteRepository.getAllNotes()

    fun delete(noteId: Long)  {
        viewModelScope.launch {
            noteRepository.delete(noteId).collectLatest {
                when(it){
                    is Resource.Success -> {
                        Timber.d("Note deleted successfully.")
                    }
                    is Resource.Error -> {
                        Timber.e("Error deleting note: ${it.exception}")
                    }
                    is Resource.Loading -> {
                        Timber.d("Deleting note.")
                    }
                }
            }
        }
    }

    fun makeUserLoggedIn() {
        sharedPreferenceRepository.makeUserLoggedIn()
    }

    fun makeUserLoggedOut() {
        sharedPreferenceRepository.makeUserLoggedOut()
    }
}