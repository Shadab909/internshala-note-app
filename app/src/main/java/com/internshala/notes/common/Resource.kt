package com.internshala.notes.common

//sealed class Resource<T> {
//    data class Success<T>(val data: T) : Resource<T>()
//    data class Error<T>(val message: String, val data: T? = null) : Resource<T>()
//    object Loading : Resource<Nothing>()
//}

sealed class Resource<out T> {
    data class Success<out R>(val data: R) : Resource<R>()
    data class Error(val exception: Exception) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}