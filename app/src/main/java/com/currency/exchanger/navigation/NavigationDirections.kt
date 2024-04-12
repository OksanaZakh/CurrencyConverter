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

    val exchangeratesList = NavigationDirection(destination = "exchangerateListScreen")

    fun exchangerate(itemId: String = "{itemId}"): NavigationDirection {
        return NavigationDirection(
            destination = "exchangerate/$itemId",
            arguments = listOf(
                navArgument("itemId") {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                },
            ),
        )
    }
}