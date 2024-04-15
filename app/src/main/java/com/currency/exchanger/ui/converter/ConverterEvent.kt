package com.currency.exchanger.ui.converter

sealed interface ConverterEvent {
    data object GetExchangeRates : ConverterEvent
    data object GetDetailedBalances : ConverterEvent
    data class Calculate (val receiveCurrency: String, val sellAmount: Double ): ConverterEvent
    data class SubmitConversion(val receiveCurrency: String, val sellAmount: Double) : ConverterEvent
    data class ShowInfoPopup(val message: String) : ConverterEvent
}