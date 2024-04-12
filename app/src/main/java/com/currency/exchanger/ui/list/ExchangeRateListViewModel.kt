package com.currency.exchanger.ui.list

import androidx.lifecycle.viewModelScope
import com.currency.exchanger.domain.repo.ExchangeRateRepository
import com.currency.exchanger.navigation.ExchangeRatesDirections
import com.currency.exchanger.navigation.NavigationManager
import com.currency.exchanger.ui.BaseViewModel
import com.currency.exchanger.di.coroutines.IoDispatcher
import com.currency.exchanger.domain.model.ExchangeRateResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.update

@HiltViewModel
class ExchangeRateListViewModel @Inject constructor(
    private val exchangerateRepository: ExchangeRateRepository,
    private val navigationManager: NavigationManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseViewModel<ExchangeRateListState, ExchangeRateListEvent>(ExchangeRateListState()) {

    override fun onEvent(event: ExchangeRateListEvent) {
        when (event) {
            ExchangeRateListEvent.GetItems -> getItems()
            is ExchangeRateListEvent.ItemPressed -> navigateToItem(event.item)
        }
    }

    private fun getItems() {
        viewModelScope.launch(dispatcher) {
            _state.update { it.copy(isLoading = true) }
            when (val response = exchangerateRepository.getExchangeRates()) {
                is ExchangeRateResponse.Success -> {
                    _state.update {
                        it.copy(
                            items = response.data.rates.map {it.key },
                            isLoading = false
                        )
                    }
                }

                is ExchangeRateResponse.Error -> {
                    _state.update { it.copy(errorMessage = response.message, isLoading = false) }
                }
            }
        }
    }

    private fun navigateToItem(item: String) {
        navigationManager.navigate(ExchangeRatesDirections.exchangerate(item))
    }
}