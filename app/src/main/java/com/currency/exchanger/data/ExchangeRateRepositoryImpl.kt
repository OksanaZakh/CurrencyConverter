package com.currency.exchanger.data

import com.currency.exchanger.data.net.NetworkExchangeRateDataSource
import com.currency.exchanger.domain.model.ExchangeRateModel
import com.currency.exchanger.domain.model.ExchangeRateResponse
import com.currency.exchanger.domain.repo.ExchangeRateRepository
import javax.inject.Inject

class ExchangeRateRepositoryImpl @Inject constructor(

    private val networkExchangeRateDataSource: NetworkExchangeRateDataSource,
) : ExchangeRateRepository {

    override suspend fun getExchangeRates(): ExchangeRateResponse<ExchangeRateModel> {
        return networkExchangeRateDataSource.getExchangeRate()
    }
}