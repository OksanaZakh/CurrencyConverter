package com.currency.exchanger.domain.repo

import com.currency.exchanger.domain.model.ExchangeRateResponse
import com.currency.exchanger.domain.model.ExchangeRateModel

interface ExchangeRateRepository {
    suspend fun getExchangeRates(): ExchangeRateResponse<ExchangeRateModel>
}