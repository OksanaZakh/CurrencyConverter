package com.currency.exchanger.domain.usecase

import com.currency.exchanger.data.ConverterDataStore
import javax.inject.Inject

class CalculateCommissionUseCase @Inject constructor(
    private val dataStore: ConverterDataStore,
) : BaseUseCase<Pair<Double, Boolean>, Double> {
    override suspend fun execute(params: Pair<Double, Boolean>): Double {
        val attempt = dataStore.getNumAttempts()
        val commission =
            if (attempt < NUM_ATTEMPTS_FOR_FREE) 0.0 else params.first * COMMISSION_RATE
        if (params.second) dataStore.setNumAttempts(attempt + 1)
        return FORMAT.format(commission).toDouble()
    }

    companion object {
        const val FORMAT = "%.${2}f"
        const val COMMISSION_RATE = 0.007
        const val NUM_ATTEMPTS_FOR_FREE = 5
    }
}