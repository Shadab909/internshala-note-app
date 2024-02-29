package com.internshala.notes.presentation.screen.notes_list_screen

import com.internshala.notes.domain.model.Note

interface NoteItemClickListener {
    fun onNoteLongClick(noteId: Long)
    fun onNoteClick(note: Note)
}