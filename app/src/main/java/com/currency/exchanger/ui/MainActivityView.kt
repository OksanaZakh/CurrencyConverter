package com.currency.exchanger.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.currency.exchanger.navigation.AppNavigation
import com.currency.exchanger.navigation.NavigationCommand
import com.currency.exchanger.navigation.NavigationManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun MainActivityView(navigationManager: NavigationManager) {
    val navController = rememberNavController()

    LaunchedEffect("Navigation event") {
        navigationManager.command.onEach { route ->
            when (route.second) {
                NavigationManager.DirectionType.DEFAULT -> {
                    route.first?.let { navController.navigate(it) }
                }

                NavigationManager.DirectionType.BACK -> {
                    route.first?.let { navController.popBackStack(it.destination, true) }
                        ?: navController.popBackStack()
                }
            }
        }.launchIn(this)
    }

    Scaffold(
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                AppNavigation(navController = navController)
            }
        }
    )
}

private fun NavHostController.navigate(navigationCommand: NavigationCommand) {
    if (!popBackStack(navigationCommand.destination, false)) {
        navigate(navigationCommand.destination) {
            navigationCommand.popUpToRoot?.let {
                if (it) {
                    popUpTo(0)
                } else {
                    popUpTo(navigationCommand.destination)
                }
            }
            launchSingleTop = true
        }
    }
}