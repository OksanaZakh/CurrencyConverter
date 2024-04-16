package com.currency.exchanger.ui.converter

import androidx.lifecycle.viewModelScope
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
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseViewModel<ConverterState, ConverterEvent>(ConverterState.Default) {

    private val _balance: MutableStateFlow<List<Pair<String, Double>>> =
        MutableStateFlow(emptyList())
    val balance = _balance.asStateFlow()

    private val _rates: MutableStateFlow<List<Pair<String, Double>>> = MutableStateFlow(emptyList())
    val rates = _rates.asStateFlow()

    private val _receive: MutableStateFlow<Pair<String, Double>> =
        MutableStateFlow(INITIAL_RECEIVE_CURRENCY to 0.0)
    val receive = _receive.asStateFlow()

    private val _sell: MutableStateFlow<Pair<String, Double>> =
        MutableStateFlow(INITIAL_CURRENCY to 0.0)
    val sell = _sell.asStateFlow()

    init {
        getCurrentBalance()
        getExchangeRates()
    }

    override fun onEvent(event: ConverterEvent) {
        when (event) {
            is ConverterEvent.GetExchangeRates -> getExchangeRates()
            is ConverterEvent.Calculate -> calculate(event.receiveCurrency, event.sellAmount)
            is ConverterEvent.ShowInfoPopup -> showInfoPopUp()
            is ConverterEvent.SubmitConversion -> submitConversion()
            is ConverterEvent.DismissPopup -> dismissPopup()
        }
    }

    private fun dismissPopup() {
        _state.value = ConverterState.Default
    }

    private fun submitConversion() {
        _state.value = ConverterState.Default
        viewModelScope.launch(dispatcher) {
            val commission = calculateCommission(submit = true)
            val balance: Double =
                _balance.value.firstOrNull { it.first == _sell.value.first }?.second ?: 0.0
            if (commission >= 0 && ((commission + _sell.value.second) <= balance)) {
                _state.value = ConverterState.Loading
                updateBalance(commission)
                _sell.value = _sell.value.first to 0.0
                _receive.value = _receive.value.first to 0.0
                _state.value = ConverterState.Default
            } else {
                _state.value = ConverterState.Default
            }
        }
    }

    private suspend fun updateBalance(commission: Double) {
        val list: MutableList<Pair<String, Double>> = mutableListOf()
        val newReceiveAmount =
            (_balance.value.firstOrNull { it.first == receive.value.first }?.second
                ?: 0.0) + receive.value.second
        val newSellAmount = (_balance.value.firstOrNull { it.first == sell.value.first }?.second
            ?: 0.0) - sell.value.second - commission

        balance.value.forEach {
            when (it.first) {
                _sell.value.first -> list.add(_sell.value.first to newSellAmount.round(2))
                _receive.value.first -> list.add(_receive.value.first to newReceiveAmount.round(2))
                else -> list.add(it)
            }
        }

        if (list.firstOrNull { it.first == _receive.value.first } == null) {
            list.add(_receive.value.first to newReceiveAmount.round(2))
        }
        _balance.value = list
        updateBalanceUseCase.execute(list)
    }

    private fun showInfoPopUp() {
        viewModelScope.launch(dispatcher) {
            val commission = calculateCommission(false)
            val balance: Double =
                _balance.value.firstOrNull { it.first == _sell.value.first }?.second ?: 0.0
            if (commission >= 0 && ((commission + _sell.value.second) <= balance)) {
                _state.value = ConverterState.InfoPopup(
                    "You have converted ${_sell.value.second} ${_sell.value.first} " + "to ${_receive.value.second} ${_receive.value.first}." +
                            " Commission Fee: $commission ${_sell.value.first}  "
                )
            } else {
                _state.value = ConverterState.InfoPopup(
                    "You have not enough money on your ${_sell.value.first} account. Please insert another amount."
                )
            }
        }
    }

    private suspend fun calculateCommission(submit: Boolean): Double {
        val attempt = getNumAttemptsUseCase.execute(Unit)
        val commission = if (attempt < 5) 0.0 else _sell.value.second * COMMISSION_RATE
        if (submit) setNumAttemptsUseCase.execute(attempt + 1)
        return commission.round(2)
    }

    private fun calculate(currency: String, amount: Double) {
        val balance = balance.value.firstOrNull { it.first == INITIAL_CURRENCY }?.second
        if (balance != null && balance >= amount) {
            val rate = rates.value.firstOrNull { it.first == currency }?.second
            val newAmount = rate?.let {
                (it * amount).round(2)
            } ?: 0.0
            _sell.value = INITIAL_CURRENCY to amount
            _receive.value = currency to newAmount
        }
    }

    private fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

    private fun getCurrentBalance() {
        viewModelScope.launch(dispatcher) {
            val map = getBalanceUseCase.execute(Unit)
            if (map.isEmpty()) {
                val initialBalance = listOf(INITIAL_CURRENCY to INITIAL_AMOUNT)
                updateBalanceUseCase.execute(initialBalance)
                _balance.value = initialBalance
            } else {
                _balance.value = map
            }
        }
    }

    private fun getExchangeRates() {
        if (_state.value is ConverterState.Default || _state.value is ConverterState.Error) {
            viewModelScope.launch(
                dispatcher
            ) {
                val response = getExchangeRateUseCase.execute(Unit)
                when (response) {
                    is ExchangeRateResponse.Success -> {
                        _state.value = ConverterState.Default
                        _rates.value = response.data.rates.map { it.toPair() }
                        calculate(_receive.value.first, _sell.value.second)
                    }

                    is ExchangeRateResponse.Error -> _state.value = ConverterState.Error(
                        response.message ?: ERROR_MESSAGE
                    )
                }
            }
        }
    }

    companion object {
        const val INITIAL_CURRENCY = "EUR"
        const val COMMISSION_RATE = 0.007
        const val INITIAL_RECEIVE_CURRENCY = "USD"
        const val INITIAL_AMOUNT = 1000.0
        const val ERROR_MESSAGE = "Something goes wrong:( Please try again later."
    }
}