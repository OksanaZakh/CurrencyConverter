package com.currency.exchanger.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

data class NavigationDirection(
    override val destination: String,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override val popUpToRoot: Boolean? = null,
) : NavigationCommand

object ExchangeRatesDirections {
    val converter = NavigationDirection(destination = "converterScreen")
    val balances = NavigationDirection(destination = "balancesScreen")
}