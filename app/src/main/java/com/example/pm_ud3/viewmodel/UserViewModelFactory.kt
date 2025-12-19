package com.example.pm_ud3.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pm_ud3.data.AppContainer

class UserViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val container = AppContainer(application)
        return UserViewModel(container.userRepository) as T
    }
}