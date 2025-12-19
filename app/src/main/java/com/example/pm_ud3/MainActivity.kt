package com.example.pm_ud3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pm_ud3.ui.navigation.AppNavGraph
import com.example.pm_ud3.viewmodel.UserViewModel
import com.example.pm_ud3.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: UserViewModel = viewModel(
                factory = UserViewModelFactory(application)
            )

            AppNavGraph(viewModel = viewModel)
        }
    }
}