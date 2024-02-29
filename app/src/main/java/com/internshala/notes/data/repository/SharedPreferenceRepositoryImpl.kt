package com.internshala.notes.data.repository

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.internshala.notes.common.Constants.PREF_IS_USER_LOGGED_IN
import com.internshala.notes.domain.repository.SharedPreferenceRepository

class SharedPreferenceRepositoryImpl(application: Application) : SharedPreferenceRepository {
    private val sharedPreferences = application.getSharedPreferences("notes", MODE_PRIVATE)
    override fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(PREF_IS_USER_LOGGED_IN, false)
    }

    override fun makeUserLoggedIn() {
        sharedPreferences.edit().putBoolean(PREF_IS_USER_LOGGED_IN, true).apply()
    }

    override fun makeUserLoggedOut() {
        sharedPreferences.edit().putBoolean(PREF_IS_USER_LOGGED_IN, false).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: SharedPreferenceRepositoryImpl? = null

        fun getInstance(application: Application): SharedPreferenceRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                val instance = SharedPreferenceRepositoryImpl(application)
                INSTANCE = instance
                instance
            }
        }
    }
}