package com.currency.exchanger.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.currency.exchanger.ui.balances.BalancesScreen
import com.currency.exchanger.ui.converter.ConverterScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = ExchangeRatesDirections.converter.destination,
    ) {
        composable(ExchangeRatesDirections.converter.destination) { ConverterScreen() }
        composable(ExchangeRatesDirections.balances.destination) { BalancesScreen() }
    }
}