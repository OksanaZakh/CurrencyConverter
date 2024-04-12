package com.currency.exchanger.domain.usecase

import com.currency.exchanger.data.ConverterDataStore
import javax.inject.Inject

class UpdateBalanceUseCase@Inject constructor(
    private val dataStore: ConverterDataStore
) : BaseUseCase<Map<String, Double>, Unit> {
    override suspend fun execute(params: Map<String, Double>) {
        dataStore.setBalance(params)
    }
}