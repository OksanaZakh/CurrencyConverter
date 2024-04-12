package com.currency.exchanger.ui.converter

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.currency.exchanger.R
import com.currency.exchanger.ui.theme.ConverterTheme
import com.currency.exchanger.ui.theme.ConverterTypography
import java.util.Currency
import java.util.Locale

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val balance by viewModel.balance.collectAsStateWithLifecycle()
    val rates by viewModel.rates.collectAsStateWithLifecycle()

    ConverterScreenContent(
        balance = balance,
        rates = rates,
        popup = (state as? ConverterState.InfoPopup)?.message,
        isLoading = state is ConverterState.Loading,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun ConverterScreenContent(
    balance: Map<String, Double> = emptyMap(),
    rates: Map<String, Double> = emptyMap(),
    popup: String? = null,
    isLoading: Boolean = false,
    onEvent: (ConverterEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ConverterTheme.colors.primaryContainer,
                    titleContentColor = ConverterTheme.colors.background
                )
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = ConverterTheme.colors.primaryContainer)
            ) {
                Text(
                    text = stringResource(R.string.submit).uppercase(Locale.getDefault()),
                    style = ConverterTheme.typography.headlineMedium
                )
            }
        }
    ) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 8.dp)
                .background(color = ConverterTheme.colors.background)
        ) {
            Title(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.my_balances)
            )
            LazyRow {
                items(balance.size) {
                    val item = balance.toList()[it]
                    BalanceItem(
                        currency = item.first,
                        rate = item.second
                    )
                }
            }
            Title(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.currency_exchange)
            )

            LazyColumn {
                items(rates.size) {
                    val item = rates.toList()[it]
                    BalanceItem(
                        currency = item.first,
                        rate = item.second
                    )
                }
            }
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
}

@Composable
fun Title(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        color = ConverterTheme.colors.secondary,
        style = ConverterTheme.typography.body
    )
}

@Composable
fun BalanceItem(currency: String, rate: Double) {
    Row(modifier = Modifier.padding(start = 16.dp, end = 24.dp)) {
        Text(
            modifier = Modifier,
            text = currency,
            color = ConverterTheme.colors.onTertiaryContainer,
            style = ConverterTheme.typography.body
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = rate.toString(),
            color = ConverterTheme.colors.onTertiaryContainer,
            style = ConverterTheme.typography.body
        )
    }
}

@Preview
@Composable
private fun ExchangeRateListScreenPreview() {
    ConverterScreenContent(
        isLoading = false,
        onEvent = {},
    )
}