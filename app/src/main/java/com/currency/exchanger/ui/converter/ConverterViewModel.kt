package com.currency.exchanger.ui.converter

import androidx.lifecycle.viewModelScope
import com.currency.exchanger.ui.BaseViewModel
import com.currency.exchanger.di.coroutines.IoDispatcher
import com.currency.exchanger.domain.model.ExchangeRateResponse
import com.currency.exchanger.domain.usecase.CalculateCommissionUseCase
import com.currency.exchanger.domain.usecase.GetBalanceUseCase
import com.currency.exchanger.domain.usecase.GetExchangeRateUseCase
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
    private val calculateCommissionUseCase: CalculateCommissionUseCase,
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
            is ConverterEvent.ShowInfoPopup -> showInfoPopUp()
            is ConverterEvent.SubmitConversion -> submitConversion()
            is ConverterEvent.DismissPopup -> dismissPopup()
            is ConverterEvent.Calculate -> calculate(
                event.receiveCurrency,
                event.sellCurrency,
                event.sellAmount
            )
        }
    }

    private fun dismissPopup() {
        _state.value = ConverterState.Default
    }

    private fun submitConversion() {
        _state.value = ConverterState.Default
        viewModelScope.launch(dispatcher) {
            val commission = calculateCommissionUseCase.execute(_sell.value.second to true)
            val balance = currentBalanceOfSellCurr()
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
            val commission = calculateCommissionUseCase.execute(_sell.value.second to false)
            val balance = currentBalanceOfSellCurr()
            if (commission >= 0.0 && ((commission + _sell.value.second) <= balance)) {
                _state.value = ConverterState.InfoPopup(
                    YOU_HAVE_CONVERTED + " ${_sell.value.second} ${_sell.value.first} "
                            + TO + " ${_receive.value.second} ${_receive.value.first}." +
                            " " + COMMISSION_FEE + " $commission ${_sell.value.first}  "
                )
            } else {
                _state.value = ConverterState.InfoPopup(
                    String.format(
                        ERROR_NOT_ENOUGH_MONEY,
                        _sell.value.first,
                    )
                )
            }
        }
    }

    private fun currentBalanceOfSellCurr() =
        _balance.value.firstOrNull { it.first == _sell.value.first }?.second ?: 0.0

    private fun calculate(receiveCurrency: String, sellCurrency: String, amount: Double) {
        val balance = balance.value.firstOrNull { it.first == sellCurrency }?.second
        if (balance != null && balance >= amount) {
            val rate = rates.value.firstOrNull { it.first == receiveCurrency }?.second ?: 0.0
            val newAmount = if (sellCurrency == INITIAL_CURRENCY) {
                (rate * amount).round(2)
            } else {
                val sellRate = rates.value.firstOrNull { it.first == sellCurrency }?.second ?: 1.0
                val crossRate = rate / sellRate
                (crossRate * amount).round(2)
            }
            _sell.value = sellCurrency to amount
            _receive.value = receiveCurrency to newAmount
        }
    }


    private fun getCurrentBalance() {
        viewModelScope.launch(dispatcher) {
            _balance.value = getBalanceUseCase.execute(Unit)
        }
    }

    private fun getExchangeRates() {
        if (_state.value is ConverterState.Default || _state.value is ConverterState.Error) {
            viewModelScope.launch(dispatcher) {
                val response = getExchangeRateUseCase.execute(Unit)
                when (response) {
                    is ExchangeRateResponse.Success -> {
                        _state.value = ConverterState.Default
                        _rates.value = response.data.rates.map { it.toPair() }
                        calculate(_receive.value.first, sell.value.first, _sell.value.second)
                    }

                    is ExchangeRateResponse.Error -> _state.value = ConverterState.Error(
                        response.message ?: ERROR_MESSAGE
                    )
                }
            }
        }
    }

    private fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

    companion object {
        const val INITIAL_CURRENCY = "EUR"
        const val INITIAL_RECEIVE_CURRENCY = "USD"
        const val ERROR_MESSAGE = "Something goes wrong:( Please try again later."
        const val ERROR_NOT_ENOUGH_MONEY =
            "You have not enough money on your %s account. Please insert another amount."
        const val YOU_HAVE_CONVERTED = "You have converted"
        const val TO = "to"
        const val COMMISSION_FEE = "Commission Fee:"
    }
}