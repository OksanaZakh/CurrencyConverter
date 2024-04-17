package com.currency.exchanger.domain.usecase

import com.currency.exchanger.data.ConverterDataStore
import javax.inject.Inject

class GetBalanceUseCase @Inject constructor(
    private val dataStore: ConverterDataStore,
    private val updateBalanceUseCase: UpdateBalanceUseCase,
) : BaseUseCase<Unit, List<Pair<String, Double>>> {
    override suspend fun execute(params: Unit): List<Pair<String, Double>> {
        val map = dataStore.getBalance()
        return map.ifEmpty {
            val initialBalance =
                listOf(INITIAL_CURRENCY to INITIAL_AMOUNT)
            updateBalanceUseCase.execute(initialBalance)
            initialBalance
        }
    }

    companion object {
        const val INITIAL_AMOUNT = 1000.0
        const val INITIAL_CURRENCY = "EUR"
    }
}
