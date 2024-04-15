package com.currency.exchanger.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConverterDataStore @Inject constructor(
    @ApplicationContext var context: Context,
) {

    private val Context.dataStore by preferencesDataStore(
        name = CURRENCY_CONVERTER
    )

    suspend fun getNumAttempts(): Int {
        return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.numAttemptsKey)
            ?: 0
    }

    suspend fun setNumAttempts(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.numAttemptsKey] = value
        }
    }

    suspend fun getBalance(): List<Pair<String, Double>> {
        return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.balanceKey)
            ?.toBalancePairList()
            ?: emptyList()
    }

    suspend fun setBalance(value: List<Pair<String, Double>>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.balanceKey] = value.toString()
        }
    }

    private fun String.toBalancePairList(): List<Pair<String, Double>> {
        return try {
            this.replace("[(", "")
                .replace(")]", "").replace(" ", "")
                .split("),(").map { text ->
                    Pair(
                        text.substringBefore(",").trim(),
                        text.substringAfter(",").trim().toDouble()
                    )
                }
        } catch (e: Exception) {
            return emptyList()
        }
    }

    companion object {
        const val CURRENCY_CONVERTER = "currency_converter"
    }
}

object PreferencesKeys {
    val numAttemptsKey = intPreferencesKey("numAttemptsKey")
    val balanceKey = stringPreferencesKey("balanceKey")
}