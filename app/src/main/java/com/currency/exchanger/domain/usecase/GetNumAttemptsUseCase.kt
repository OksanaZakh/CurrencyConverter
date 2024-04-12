package com.currency.exchanger.domain.usecase

import com.currency.exchanger.data.ConverterDataStore
import javax.inject.Inject

class GetNumAttemptsUseCase @Inject constructor(
    private val dataStore: ConverterDataStore
) : BaseUseCase<Unit, Int> {
    override suspend fun execute(params: Unit): Int {
        return dataStore.getNumAttempts()
    }
}