package com.currency.exchanger.ui.exchangerate

sealed interface ExchangeRateEvent {

    data object NavigateBack : ExchangeRateEvent

    data class SetItem(val item: String) : ExchangeRateEvent
}