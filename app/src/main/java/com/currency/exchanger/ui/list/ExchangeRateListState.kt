package com.currency.exchanger.ui.list

data class ExchangeRateListState(
    val isLoading: Boolean = false,
    val items: List<String> = emptyList(),
    val errorMessage: String? = null
)