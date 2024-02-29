package com.internshala.notes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long
) : Parcelable