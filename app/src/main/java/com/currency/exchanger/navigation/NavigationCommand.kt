package com.currency.exchanger.navigation

import androidx.navigation.NamedNavArgument

interface NavigationCommand {
    val destination: String
    var arguments: List<NamedNavArgument>
    val popUpToRoot: Boolean?
}