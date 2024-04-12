package com.currency.exchanger.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.currency.exchanger.ui.theme.ExchangeRateTheme
import androidx.compose.ui.unit.dp

@Composable
fun ExchangeRateListItem(
    modifier: Modifier = Modifier,
    item: String,
    onClick: (String) -> Unit,
) {
    Card(
        modifier = modifier.clickable { onClick(item) },
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(vertical = 18.dp),
            text = item,
            color = ExchangeRateTheme.colors.onPrimaryContainer,
            style = ExchangeRateTheme.typography.headlineMedium,
        )
    }
}