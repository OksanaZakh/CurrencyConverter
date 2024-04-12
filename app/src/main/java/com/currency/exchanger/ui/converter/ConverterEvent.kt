package com.currency.exchanger.ui.converter

sealed interface ConverterEvent {
    data object GetExchangeRates : ConverterEvent
    data object GetDetailedBalances : ConverterEvent
    data object DismissError : ConverterEvent
    data class SubmitConversion(val from: String, val to: String) : ConverterEvent
    data class ShowInfoPopup(val message: String) : ConverterEvent
}