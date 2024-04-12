package com.currency.exchanger.ui.exchangerate

import com.currency.exchanger.domain.repo.ExchangeRateRepository
import com.currency.exchanger.navigation.NavigationManager
import com.currency.exchanger.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
    private val exchangerateRepository: ExchangeRateRepository,
    private val navigationManager: NavigationManager,
) : BaseViewModel<ExchangeRateState, ExchangeRateEvent>(ExchangeRateState()) {

    override fun onEvent(event: ExchangeRateEvent) {
        when (event) {
            ExchangeRateEvent.NavigateBack -> navigationManager.navigateBack()
            is ExchangeRateEvent.SetItem -> _state.update { it.copy(item = event.item) }
        }
    }
}