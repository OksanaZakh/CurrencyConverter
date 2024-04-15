package com.currency.exchanger.ui.converter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.currency.exchanger.R
import com.currency.exchanger.ui.theme.ConverterTheme
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

    ConverterScreenContent(
        balance = balance,
        rates = rates,
        receive = receive,
        sell = sell,
        popup = (state as? ConverterState.InfoPopup)?.message,
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
    isLoading: Boolean = false,
    onEvent: (ConverterEvent) -> Unit,
) {
    Scaffold(modifier = Modifier.systemBarsPadding(), topBar = {
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
                .height(56.dp),
            onClick = {},
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
                amount = sell.second,
                maxSellAmount = 1000.0,
                isInputEnabled = true,
                currency = sell.first,
                currencyList = listOf(sell.first),
                onValueChange = { _, value ->
                    onEvent(
                        ConverterEvent.Calculate(
                            receive.first,
                            value
                        )
                    )
                }
            )

            Divider(modifier = Modifier.padding(16.dp))

            ExchangeRow(
                icon = R.drawable.ic_arrow_down_circle,
                color = ConverterTheme.colors.onPrimaryContainer,
                text = stringResource(R.string.receive),
                amount = receive.second,
                isInputEnabled = false,
                currency = receive.first,
                currencyList = rates.map { it.first },
                onValueChange = { currency, _ ->
                    onEvent(
                        ConverterEvent.Calculate(
                            currency,
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
}

@Composable
fun ExchangeRow(
    @DrawableRes icon: Int,
    color: Color,
    text: String,
    amount: Double,
    maxSellAmount: Double = 1000.0,
    isInputEnabled: Boolean,
    currency: String,
    currencyList: List<String>,
    onValueChange: (String, Double) -> Unit = { _, _ -> }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(icon),
                contentDescription = null,
                tint = color
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = text,
                color = ConverterTheme.colors.onTertiaryContainer,
                style = ConverterTheme.typography.body
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isInputEnabled) {
                NumberInputField(
                    maxAmount = maxSellAmount,
                    initialValue = amount,
                    onValueChange = { onValueChange(currency, it) })
            } else {
                Text(
                    modifier = Modifier.width(96.dp),
                    text = "+ $amount",
                    color = ConverterTheme.colors.onPrimaryContainer,
                    style = ConverterTheme.typography.body
                )
            }

            if (currencyList.isNotEmpty()) {
                CurrencyDropdown(selectedCurrency = currency,
                    items = currencyList,
                    onItemSelected = { if (currencyList.size > 1) onValueChange(it, 0.0) })
            }
        }
    }
}

@Composable
fun NumberInputField(
    maxAmount: Double = 1000.0,
    initialValue: Double = 0.0,
    onValueChange: (Double) -> Unit = {}
) {
    val amount = remember { mutableDoubleStateOf(initialValue) }
    val isError = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val change: (String) -> Unit = { it ->
        val inputAmount = it.toDoubleOrNull()
        amount.doubleValue = inputAmount ?: 0.0
        if (inputAmount != null && inputAmount >= 0.0 && inputAmount <= maxAmount) {
            onValueChange(inputAmount)
            isError.value = false
        } else {
            isError.value = true
        }
    }

    TextField(
        modifier = Modifier.width(96.dp),
        textStyle = ConverterTheme.typography.body,
        value = amount.doubleValue.toString(),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Decimal,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            },
        ),
        colors = TextFieldDefaults.colors(
            errorTextColor = ConverterTheme.colors.error,
            errorContainerColor = ConverterTheme.colors.tertiary,
            focusedContainerColor = ConverterTheme.colors.tertiary,
            unfocusedContainerColor = ConverterTheme.colors.tertiary,
            focusedIndicatorColor = ConverterTheme.colors.tertiary,
            unfocusedIndicatorColor = ConverterTheme.colors.tertiary,
            disabledIndicatorColor = ConverterTheme.colors.tertiary
        ),
        isError = isError.value,
        onValueChange = change
    )
}

@Composable
fun CurrencyDropdown(
    selectedCurrency: String,
    items: List<String> = mutableListOf(selectedCurrency),
    onItemSelected: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(items.indexOf(selectedCurrency)) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        Row(
            modifier = Modifier.clickable(onClick = { expanded = true }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = items[selectedIndex],
                modifier = Modifier.background(ConverterTheme.colors.tertiary),
                color = ConverterTheme.colors.onTertiaryContainer,
                style = ConverterTheme.typography.body
            )

            Icon(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(16.dp),
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                tint = ConverterTheme.colors.onTertiaryContainer
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(96.dp)
                .background(ConverterTheme.colors.background)
        ) {
            items.forEachIndexed { index, currency ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    onItemSelected(items[selectedIndex])
                    expanded = false
                }, text = {
                    Text(
                        text = currency,
                        color = ConverterTheme.colors.onTertiaryContainer,
                        style = ConverterTheme.typography.body
                    )
                })
            }
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