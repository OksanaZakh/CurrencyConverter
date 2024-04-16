package com.currency.exchanger.ui.converter

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.currency.exchanger.R
import com.currency.exchanger.ui.theme.ConverterTheme
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val balance by viewModel.balance.collectAsStateWithLifecycle()
    val rates by viewModel.rates.collectAsStateWithLifecycle()
    val receive by viewModel.receive.collectAsStateWithLifecycle()
    val sell by viewModel.sell.collectAsStateWithLifecycle()

    val lifecycleEvent = rememberLifecycleEvent()

    LaunchedEffect(lifecycleEvent) {
        while (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
            viewModel.onEvent(ConverterEvent.GetExchangeRates)
            delay(UPDATE_THRESHOLD)
        }
    }

    ConverterScreenContent(
        balance = balance,
        rates = rates,
        receive = receive,
        sell = sell,
        popup = (state as? ConverterState.InfoPopup)?.message,
        error = (state as? ConverterState.Error)?.message,
        isLoading = state is ConverterState.Loading,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun ConverterScreenContent(
    balance: List<Pair<String, Double>> = emptyList(),
    rates: List<Pair<String, Double>> = emptyList(),
    sell: Pair<String, Double> = "EUR" to 0.0,
    receive: Pair<String, Double> = "USD" to 0.0,
    popup: String? = null,
    error: String? = null,
    isLoading: Boolean = false,
    onEvent: (ConverterEvent) -> Unit,
) {

    var isErrorAmount by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(modifier = Modifier
        .systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.currency_converter),
                        style = ConverterTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ConverterTheme.colors.primaryContainer,
                    titleContentColor = ConverterTheme.colors.background
                )
            )
        }, bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp)
                    .imePadding(),
                enabled = !isErrorAmount && sell.second > 0.0,
                onClick = {
                    focusManager.clearFocus(true)
                    onEvent(ConverterEvent.ShowInfoPopup)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ConverterTheme.colors.primaryContainer)
            ) {
                Text(
                    text = stringResource(R.string.submit).uppercase(Locale.getDefault()),
                    style = ConverterTheme.typography.headlineMedium
                )
            }
        }) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 8.dp)
                .background(color = ConverterTheme.colors.background)
        ) {
            Title(
                modifier = Modifier.padding(16.dp), text = stringResource(R.string.my_balances)
            )
            LazyRow {
                items(balance.size) {
                    val item = balance[it]
                    BalanceItem(
                        currency = item.first, rate = item.second
                    )
                }
            }
            Title(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.currency_exchange)
            )

            ExchangeRow(
                icon = R.drawable.ic_arrow_up_circle,
                color = ConverterTheme.colors.error,
                text = stringResource(R.string.sell),
                amount = sell,
                maxSellAmount = balance.firstOrNull { it.first == sell.first }?.second ?: 0.0,
                isInputEnabled = true,
                focusManager = focusManager,
                currencyList = balance.filter { it.second != 0.0 }.map { it.first },
                onValueChange = { currency, value ->
                    isErrorAmount = false
                    onEvent(
                        ConverterEvent.Calculate(
                            receive.first,
                            currency,
                            value
                        )
                    )
                },
                onInvalidAmount = { isErrorAmount = true }
            )

            Divider(modifier = Modifier.padding(16.dp))

            ExchangeRow(
                icon = R.drawable.ic_arrow_down_circle,
                color = ConverterTheme.colors.onPrimaryContainer,
                text = stringResource(R.string.receive),
                amount = receive,
                focusManager = focusManager,
                isInputEnabled = false,
                currencyList = rates.map { it.first },
                onValueChange = { currency, _ ->
                    onEvent(
                        ConverterEvent.Calculate(
                            currency,
                            sell.first,
                            sell.second
                        )
                    )
                }
            )
        }
    }
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
                color = ConverterTheme.colors.primaryContainer
            )
        }
    }

    popup?.let {
        InfoPopUp(
            text = it,
            title = stringResource(R.string.currency_converted),
            confirmButtonText = stringResource(R.string.done),
            onDismiss = { onEvent(ConverterEvent.DismissPopup) },
            onConfirm = { onEvent(ConverterEvent.SubmitConversion) }
        )
    }

    error?.let {
        focusManager.clearFocus(true)
        InfoPopUp(
            text = it,
            title = stringResource(R.string.error),
            confirmButtonText = stringResource(R.string.ok),
            onDismiss = { onEvent(ConverterEvent.DismissPopup) },
            onConfirm = { onEvent(ConverterEvent.DismissPopup) }
        )
    }
}

const val UPDATE_THRESHOLD = 5000L

@Composable
fun rememberLifecycleEvent(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current): Lifecycle.Event {
    var state by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            state = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    return state
}


@Preview
@Composable
private fun ExchangeRateListScreenPreview() {
    ConverterScreenContent(
        isLoading = false,
        onEvent = {},
    )
}