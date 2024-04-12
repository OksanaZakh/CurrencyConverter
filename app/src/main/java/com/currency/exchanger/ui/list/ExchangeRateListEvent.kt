package com.currency.exchanger.ui.list

sealed interface ExchangeRateListEvent {

    data object GetItems : ExchangeRateListEvent

    data class ItemPressed(val item: String) : ExchangeRateListEvent
}