package com.currency.exchanger.domain.model

sealed class ExchangeRateResponse<T> {
    data class Success<T>(val data: T) : ExchangeRateResponse<T>()
    data class Error<T>(val message: String? = null) : ExchangeRateResponse<T>()
}