package com.currency.exchanger.domain.usecase

import com.currency.exchanger.domain.model.ExchangeRateModel
import com.currency.exchanger.domain.model.ExchangeRateResponse
import com.currency.exchanger.domain.repo.ExchangeRateRepository
import javax.inject.Inject

class GetExchangeRateUseCase @Inject constructor(
    private val repository: ExchangeRateRepository
) : BaseUseCase<Unit, ExchangeRateResponse<ExchangeRateModel>> {
    override suspend fun execute(params: Unit): ExchangeRateResponse<ExchangeRateModel> {
        return repository.getExchangeRates()
    }
}