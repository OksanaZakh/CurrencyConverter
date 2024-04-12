package com.currency.exchanger.domain.usecase

import com.currency.exchanger.data.ConverterDataStore
import javax.inject.Inject

class SetNumAttemptsUseCase @Inject constructor(
    private val dataStore: ConverterDataStore
) : BaseUseCase<Int, Unit> {
    override suspend fun execute(params: Int) {
        return dataStore.setNumAttempts(params)
    }
}