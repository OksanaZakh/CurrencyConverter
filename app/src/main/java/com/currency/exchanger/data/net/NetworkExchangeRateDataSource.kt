package com.currency.exchanger.data.net

import javax.inject.Inject
import com.currency.exchanger.domain.model.ExchangeRateModel
import com.currency.exchanger.domain.model.ExchangeRateResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Singleton
class NetworkExchangeRateDataSource @Inject constructor(
    private val ktorHttpClient: HttpClient,
    private val json: Json,
) {
    suspend fun getExchangeRate(): ExchangeRateResponse<ExchangeRateModel> {
        return fetch<ExchangeRateModel> {
            ktorHttpClient.get("") { HttpMethod.Get }
        }
    }

    private suspend inline fun <reified T> fetch(
        loadData: () -> HttpResponse
    ): ExchangeRateResponse<T> {
        return try {
            val result = loadData().bodyAsText()
            try {
                val data = json.decodeFromString(result) as T
                ExchangeRateResponse.Success(data)
            } catch (e: Exception) {
                ExchangeRateResponse.Error(e.message)
            }
        } catch (thr: Throwable) {
            ExchangeRateResponse.Error("Bad network connectivity.")
        }
    }
}
