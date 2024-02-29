package com.internshala.notes.common

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.internshala.notes.common.Constants.DAY_MILLIS
import com.internshala.notes.common.Constants.HOUR_MILLIS
import com.internshala.notes.common.Constants.MINUTE_MILLIS
import com.internshala.notes.data.repository.NotesRepositoryImpl
import com.internshala.notes.data.repository.SharedPreferenceRepositoryImpl
import com.internshala.notes.presentation.screen.note_details_screen.UpsertNoteViewModel
import com.internshala.notes.presentation.screen.notes_list_screen.NoteListViewModel
import com.internshala.notes.presentation.screen.start_screen.StartViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utils {
    fun getStartViewModelFactory(application: Application): ViewModelProvider.Factory {
        val sharedPreferenceRepositoryImpl = SharedPreferenceRepositoryImpl.getInstance(application)
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return StartViewModel(sharedPreferenceRepositoryImpl) as T
            }
        }
    }
    fun getNoteListViewModeFactory(application: Application): ViewModelProvider.Factory {
        val noteRepositoryImpl = NotesRepositoryImpl.getInstance(application)
        val sharedPreferenceRepositoryImpl = SharedPreferenceRepositoryImpl.getInstance(application)
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return NoteListViewModel(noteRepositoryImpl,sharedPreferenceRepositoryImpl) as T
            }
        }
    }
    fun getNoteDetailsViewModelFactory(application: Application): ViewModelProvider.Factory {
        val noteRepositoryImpl = NotesRepositoryImpl.getInstance(application)
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return UpsertNoteViewModel(noteRepositoryImpl) as T
            }
        }
    }

    private fun currentDate(): Date {
        val calendar: Calendar = Calendar.getInstance()
        return calendar.time
    }

    fun getDateTime(timeMillis: Long = System.currentTimeMillis()): String {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(timeMillis)
    }

    fun getTimeAgo(timeMillis: Long): String {
        var time = timeMillis
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }
        val now: Long = currentDate().time
        if (time > now || time <= 0) {
            return "in the future"
        }
        val diff = now - time
        return if (diff < MINUTE_MILLIS) {
            "Moments ago"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "A minute ago"
        } else if (diff < 50 * MINUTE_MILLIS) {
            (diff / MINUTE_MILLIS).toString() + " minutes ago"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "An hour ago"
        } else if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + " hours ago"
        } else if (diff < 48 * HOUR_MILLIS) {
            "Yesterday"
        } else {
            (diff / DAY_MILLIS).toString() + " days ago"
        }
    }
}