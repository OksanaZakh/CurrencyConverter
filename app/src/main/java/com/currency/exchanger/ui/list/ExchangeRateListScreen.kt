package com.currency.exchanger.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ExchangeRateListScreen(
    viewModel: ExchangeRateListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ExchangeRateListScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun ExchangeRateListScreenContent(
    state: ExchangeRateListState,
    onEvent: (ExchangeRateListEvent) -> Unit,
) {
    LaunchedEffect("Initial items loading") {
        onEvent(ExchangeRateListEvent.GetItems)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 12.dp),
            text = "Screen1",
        )
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.items.forEach {
                ExchangeRateListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    item = it,
                    onClick = { item -> onEvent(ExchangeRateListEvent.ItemPressed(item)) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ExchangeRateListScreenPreview() {
    ExchangeRateListScreenContent(
        state = ExchangeRateListState(),
        onEvent = {},
    )
}