package com.currency.exchanger.ui.converter

sealed interface ConverterState {
    data object Default : ConverterState

    data class InfoPopup(val message: String) : ConverterState

    data class Error(val message: String) : ConverterState

    data object Loading : ConverterState
}