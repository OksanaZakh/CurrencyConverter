package com.currency.exchanger.domain.usecase

import com.currency.exchanger.data.ConverterDataStore
import javax.inject.Inject

class GetBalanceUseCase @Inject constructor(
    private val dataStore: ConverterDataStore
) : BaseUseCase<Unit, List<Pair<String, Double>>> {
    override suspend fun execute(params: Unit): List<Pair<String, Double>> {
        return dataStore.getBalance()
    }
}
