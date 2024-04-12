package com.currency.exchanger.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateModel(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
