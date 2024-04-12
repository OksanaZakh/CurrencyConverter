package com.currency.exchanger.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.currency.exchanger.ui.exchangerate.ExchangeRateScreen
import com.currency.exchanger.ui.list.ExchangeRateListScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = ExchangeRatesDirections.exchangeratesList.destination,
    ) {
        composable(ExchangeRatesDirections.exchangeratesList.destination) { ExchangeRateListScreen() }
        composable(
            route = ExchangeRatesDirections.exchangerate().destination,
            arguments = ExchangeRatesDirections.exchangerate().arguments,
        ) { entry ->
            val item = entry.arguments?.getString("itemId")?.takeIf { it != "{itemId}" }
            ExchangeRateScreen(item = item)
        }
    }
}