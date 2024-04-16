package com.currency.exchanger.ui.converter

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.currency.exchanger.R
import com.currency.exchanger.ui.theme.ConverterTheme


@Composable
fun ExchangeRow(
    @DrawableRes icon: Int,
    color: Color,
    text: String,
    amount: Pair<String, Double>,
    focusManager: FocusManager,
    maxSellAmount: Double = 1000.0,
    isInputEnabled: Boolean,
    currencyList: List<String>,
    onValueChange: (String, Double) -> Unit = { _, _ -> },
    onInvalidAmount: () -> Unit = {}
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
                    initialValue = amount.second,
                    onValueChange = { onValueChange(amount.first, it) },
                    onInvalidAmount = onInvalidAmount,
                    focusManager = focusManager
                )

            } else {
                Text(
                    modifier = Modifier.width(96.dp),
                    text = "+ ${amount.second}",
                    color = ConverterTheme.colors.onPrimaryContainer,
                    style = ConverterTheme.typography.body
                )
            }

            if (currencyList.isNotEmpty()) {
                CurrencyDropdown(selectedCurrency = amount.first,
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
    focusManager: FocusManager = LocalFocusManager.current,
    onValueChange: (Double) -> Unit = {},
    onInvalidAmount: () -> Unit = {}
) {
    val isError = remember { mutableStateOf(false) }

    val change: (String) -> Unit = { it ->
        val inputAmount = it.toDoubleOrNull()
        if (inputAmount != null && inputAmount >= 0.0 && inputAmount <= maxAmount) {
            onValueChange(inputAmount)
            isError.value = false
        } else {
            onInvalidAmount()
            isError.value = true
        }
    }

    TextField(
        modifier = Modifier
            .width(96.dp),
        textStyle = ConverterTheme.typography.body,
        value = if (initialValue == 0.0) "" else initialValue.toString(),
        placeholder = {
            if (initialValue == 0.0) {
                Text(
                    modifier = Modifier.width(96.dp),
                    text = "0.0",
                    style = ConverterTheme.typography.body
                )
            }
        },
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

@Composable
fun InfoPopUp(
    text: String,
    title: String,
    confirmButtonText: String,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = ConverterTheme.colors.background,
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(containerColor = ConverterTheme.colors.primaryContainer)
            ) {
                Text(text = confirmButtonText)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Title(text = title)
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = text,
                    color = ConverterTheme.colors.onTertiaryContainer,
                    style = ConverterTheme.typography.body,
                    textAlign = TextAlign.Center,
                )
            }
        },
    )
}
