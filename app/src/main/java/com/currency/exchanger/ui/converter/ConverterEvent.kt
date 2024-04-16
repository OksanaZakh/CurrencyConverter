package com.currency.exchanger.ui.converter

sealed interface ConverterEvent {
    data object GetExchangeRates : ConverterEvent
    data class Calculate(
        val receiveCurrency: String,
        val sellCurrency: String,
        val sellAmount: Double
    ) : ConverterEvent

    data object SubmitConversion : ConverterEvent
    data object ShowInfoPopup : ConverterEvent
    data object DismissPopup : ConverterEvent
}