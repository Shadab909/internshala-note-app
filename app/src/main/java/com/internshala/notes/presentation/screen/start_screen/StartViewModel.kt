package com.internshala.notes.presentation.screen.start_screen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internshala.notes.domain.repository.SharedPreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartViewModel(private val sharedPreferenceRepository: SharedPreferenceRepository) : ViewModel() {
    fun isUserLoggedIn(): Boolean {
        return sharedPreferenceRepository.isUserLoggedIn()
    }

    fun makeUserLoggedIn() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferenceRepository.makeUserLoggedIn()
        }
    }

    fun makeUserLoggedOut() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferenceRepository.makeUserLoggedOut()
        }
    }
}