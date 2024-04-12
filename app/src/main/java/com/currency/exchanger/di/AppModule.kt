package com.currency.exchanger.di

import android.app.Application
import android.content.Context
import android.util.Log
import com.currency.exchanger.data.ConverterDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.currency.exchanger.data.net.NetworkExchangeRateDataSource
import com.currency.exchanger.data.ExchangeRateRepositoryImpl
import com.currency.exchanger.navigation.NavigationManager
import com.currency.exchanger.domain.repo.ExchangeRateRepository

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesNavigationManager() = NavigationManager()

    @Provides
    fun providesExchangeRateRepository(networkExchangeRateDataSource: NetworkExchangeRateDataSource): ExchangeRateRepository {
        return ExchangeRateRepositoryImpl(networkExchangeRateDataSource)
    }

    @Singleton
    @Provides
    fun providesDataStoreManager(context: Context) = ConverterDataStore(context)

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient {
            expectSuccess = true
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("Ktor", message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest {
                url("https://developers.paysera.com/tasks/api/currency-exchange-rates")
            }
        }
    }

    @Provides
    fun provideJson() = Json { ignoreUnknownKeys = true }

    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application
    }
}