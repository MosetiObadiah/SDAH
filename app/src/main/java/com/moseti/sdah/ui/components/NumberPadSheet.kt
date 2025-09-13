package com.moseti.sdah.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberPadSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onHymnSelect: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var enteredNumber by remember(showSheet) { mutableStateOf("") }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            NumberPadContent(
                enteredNumber = enteredNumber,
                onNumberClick = { num ->
                    if (enteredNumber.length < 3) {
                        enteredNumber += num
                    }
                },
                onClear = { enteredNumber = "" },
                onBackspace = {
                    if (enteredNumber.isNotEmpty()) {
                        enteredNumber = enteredNumber.dropLast(1)
                    }
                },
                onGo = {
                    enteredNumber.toIntOrNull()?.let {
                        onHymnSelect(it)
                    }
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismiss()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun NumberPadContent(
    enteredNumber: String,
    onNumberClick: (String) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onGo: () -> Unit,
) {
    val buttons = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "⌫")

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = enteredNumber.ifEmpty { "Enter Hymn #" },
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(buttons) { item ->
                NumberPadButton(
                    text = item,
                    onClick = {
                        when (item) {
                            "C" -> onClear()
                            "⌫" -> onBackspace()
                            else -> onNumberClick(item)
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onGo,
            enabled = enteredNumber.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Go to Hymn")
        }
    }
}

@Composable
private fun NumberPadButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleLarge)
    }
}