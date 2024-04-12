package com.currency.exchanger.ui.balances

sealed interface ExchangeRateEvent {

    data object NavigateBack : ExchangeRateEvent

    data class SetItem(val item: String) : ExchangeRateEvent
}