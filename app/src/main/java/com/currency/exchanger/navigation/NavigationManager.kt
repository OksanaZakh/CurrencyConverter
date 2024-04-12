package com.currency.exchanger.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NavigationManager {
    private val _command =
        MutableSharedFlow<Pair<NavigationCommand?, DirectionType>>(extraBufferCapacity = 1)
    var command: SharedFlow<Pair<NavigationCommand?, DirectionType>> = _command.asSharedFlow()

    fun navigate(direction: NavigationCommand) {
        _command.tryEmit(direction to DirectionType.DEFAULT)
    }

    fun navigateBack(direction: NavigationCommand? = null) {
        _command.tryEmit(direction to DirectionType.BACK)
    }

    enum class DirectionType {
        DEFAULT, BACK
    }
}