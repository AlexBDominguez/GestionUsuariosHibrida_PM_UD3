package com.example.pm_ud3.data

sealed class RepositoryResult<out T> {
    data class Success<out T>(val data: T? = null) : RepositoryResult<T>()
    data class Error(val exception: Throwable) : RepositoryResult<Nothing>()
}