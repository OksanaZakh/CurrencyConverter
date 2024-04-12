package com.currency.exchanger.ui.converter

import androidx.lifecycle.viewModelScope
import com.currency.exchanger.navigation.ExchangeRatesDirections
import com.currency.exchanger.navigation.NavigationManager
import com.currency.exchanger.ui.BaseViewModel
import com.currency.exchanger.di.coroutines.IoDispatcher
import com.currency.exchanger.domain.model.ExchangeRateResponse
import com.currency.exchanger.domain.usecase.GetBalanceUseCase
import com.currency.exchanger.domain.usecase.GetExchangeRateUseCase
import com.currency.exchanger.domain.usecase.GetNumAttemptsUseCase
import com.currency.exchanger.domain.usecase.SetNumAttemptsUseCase
import com.currency.exchanger.domain.usecase.UpdateBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val getBalanceUseCase: GetBalanceUseCase,
    private val updateBalanceUseCase: UpdateBalanceUseCase,
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
    private val getNumAttemptsUseCase: GetNumAttemptsUseCase,
    private val setNumAttemptsUseCase: SetNumAttemptsUseCase,
    private val navigationManager: NavigationManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseViewModel<ConverterState, ConverterEvent>(ConverterState.Loading) {

    private val _balance: MutableStateFlow<Map<String, Double>> = MutableStateFlow(emptyMap())
    val balance = _balance.asStateFlow()

    private val _rates: MutableStateFlow<Map<String, Double>> = MutableStateFlow(emptyMap())
    val rates = _rates.asStateFlow()

    init {
        getCurrentBalance()
        getExchangeRates()
    }

    override fun onEvent(event: ConverterEvent) {
        when (event) {
            is ConverterEvent.GetExchangeRates -> getExchangeRates()
            is ConverterEvent.GetDetailedBalances -> getDetailedBalances()
            is ConverterEvent.DismissError -> {}
            is ConverterEvent.ShowInfoPopup -> {}
            is ConverterEvent.SubmitConversion -> {}
        }
    }

    private fun getCurrentBalance() {
        viewModelScope.launch(dispatcher) {
            val map = getBalanceUseCase.execute(Unit)
            if (map.isEmpty()) {
                val initialBalance = mapOf(INITIAL_CURRENCY to INITIAL_AMOUNT)
                updateBalanceUseCase.execute(initialBalance)
                _balance.value = initialBalance
            } else {
                _balance.value = map
            }
        }
    }

    private fun getExchangeRates() {
        viewModelScope.launch(dispatcher) {
            val response = getExchangeRateUseCase.execute(Unit)
            when (response) {
                is ExchangeRateResponse.Success -> {
                    _state.value = ConverterState.Default
                    _rates.value = response.data.rates
                }

                is ExchangeRateResponse.Error -> _state.value =
                    ConverterState.InfoPopup(
                        response.message ?: ERROR_MESSAGE
                    )
            }
        }
    }

    private fun getDetailedBalances() {
        navigationManager.navigate(ExchangeRatesDirections.balances)
    }

    companion object {
        const val INITIAL_CURRENCY = "EUR"
        const val INITIAL_AMOUNT = 1000.0
        const val ERROR_MESSAGE = "Something goes wrong:( Please try again later."
    }
}