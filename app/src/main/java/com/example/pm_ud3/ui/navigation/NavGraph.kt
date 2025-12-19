package com.example.pm_ud3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pm_ud3.ui.views.UserFormScreen
import com.example.pm_ud3.ui.views.UserListScreen
import com.example.pm_ud3.viewmodel.UserViewModel

@Composable
fun AppNavGraph(
    viewModel: UserViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "users"
    ) {

        //Lista
        composable("users") {
            UserListScreen(
                viewModel = viewModel,
                onAddUser = {
                    navController.navigate("user_form")
                },
                onEditUser = { userId ->
                    navController.navigate("user_form/$userId")
                }
            )
        }

        // Nuevo
        composable("user_form") {
            UserFormScreen(
                viewModel = viewModel,
                userId = null,
                onDone = {
                    navController.popBackStack()
                }
            )
        }

        // Editar
        composable(
            route = "user_form/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getString("userId")

            UserFormScreen(
                viewModel = viewModel,
                userId = userId,
                onDone = {
                    navController.popBackStack()
                }
            )
        }
    }
}