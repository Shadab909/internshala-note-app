package com.internshala.notes.presentation.screen.note_details_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internshala.notes.common.Resource
import com.internshala.notes.domain.model.Note
import com.internshala.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class UpsertNoteViewModel(private val noteRepository: NotesRepository) : ViewModel() {
    val isNoteUpsertSuccessFully : MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)
    val noteNotUpsertError : MutableSharedFlow<Exception> = MutableSharedFlow(replay = 1)
    suspend fun insert(note: Note) {
        Timber.d("Inserting note: $note")
        viewModelScope.launch {
            noteRepository.insert(note).collectLatest {
                Timber.d("Insert operation Started.")
                // Update the UI based on the result of the insert operation.
                when (it) {
                    is Resource.Success -> {
                        Timber.d("Note inserted successfully.")
                        isNoteUpsertSuccessFully.emit(true)
                    }

                    is Resource.Error -> {
                        Timber.e("Error inserting note due to ${it.exception}")
                        noteNotUpsertError.emit(it.exception)
                    }

                    is Resource.Loading -> {
                        Timber.d("Inserting note.")
                        isNoteUpsertSuccessFully.emit(false)
                    }
                }
            }
        }
    }

    suspend fun update(note: Note) {
        Timber.d("Updating note: $note")
        viewModelScope.launch {
            noteRepository.update(note).collectLatest {
                Timber.d("Update operation Started.")
                // Update the UI based on the result of the insert operation.
                when (it) {
                    is Resource.Success -> {
                        Timber.d("Note updated successfully.")
                        isNoteUpsertSuccessFully.emit(true)
                    }

                    is Resource.Error -> {
                        Timber.e("Error updating note due to ${it.exception}")
                        noteNotUpsertError.emit(it.exception)
                    }

                    is Resource.Loading -> {
                        Timber.d("Updating note.")
                        isNoteUpsertSuccessFully.emit(false)
                    }
                }
            }
        }
    }
}