package com.currency.exchanger.ui.balances

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BalancesScreen(
    viewModel: ExchangeRateViewModel = hiltViewModel(),
    item: String? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    item?.let {
        LaunchedEffect("Set item into state") {
            viewModel.onEvent(ExchangeRateEvent.SetItem(item))
        }
    }

    ExchangeRateScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun ExchangeRateScreenContent(
    state: ExchangeRateState,
    onEvent: (ExchangeRateEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { onEvent(ExchangeRateEvent.NavigateBack) },
                content = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                    )
                }
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Screen2",
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 12.dp)
                .padding(vertical = 48.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = state.item,
                    color = Color.Red,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun ExchangeRateScreenPreview() {
    ExchangeRateScreenContent(
        state = ExchangeRateState(),
        onEvent = {},
    )
}