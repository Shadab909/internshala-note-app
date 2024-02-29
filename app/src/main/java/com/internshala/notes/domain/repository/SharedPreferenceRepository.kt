package com.internshala.notes.domain.repository

interface SharedPreferenceRepository {
    fun isUserLoggedIn() : Boolean
    fun makeUserLoggedIn()
    fun makeUserLoggedOut()
}