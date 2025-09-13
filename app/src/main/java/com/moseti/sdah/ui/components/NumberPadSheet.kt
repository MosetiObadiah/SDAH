package com.moseti.sdah.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var enteredNumber by remember { mutableStateOf("") }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                enteredNumber = ""
                onDismiss()
            },
            sheetState = sheetState
        ) {
            NumberPadContent(
                enteredNumber = enteredNumber,
                onNumberClick = { num ->
                    if (enteredNumber.length < 3) {
                        enteredNumber += num
                    }
                },
                onBackspace = {
                    if (enteredNumber.isNotEmpty()) {
                        enteredNumber = enteredNumber.dropLast(1)
                    }
                },
                onGo = {
                    val number = enteredNumber.toIntOrNull()
                    if (number != null) {
                        onHymnSelect(number)
                    }
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            enteredNumber = ""
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
    onBackspace: () -> Unit,
    onGo: () -> Unit,
) {
    var buttonsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        buttonsVisible = true
    }

    val buttons = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "⌫")

    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = enteredNumber.ifEmpty { "Enter Hymn #" },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            itemsIndexed(buttons) { index, item ->
                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 200, delayMillis = index * 15)) +
                            slideInVertically(
                                initialOffsetY = { it / 3 },
                                animationSpec = tween(durationMillis = 200, delayMillis = index * 15)
                            )
                ) {
                    NumberPadButton(
                        text = item,
                        onClick = {
                            when (item) {
                                "C" -> onNumberClick("clear_all")
                                "⌫" -> onBackspace()
                                else -> onNumberClick(item)
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onGo,
            enabled = enteredNumber.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Go to Hymn", fontSize = 16.sp)
        }
    }
}

@Composable
private fun NumberPadButton(
    text: String,
    onClick: () -> Unit,
) {
    val containerColor = when (text) {
        "C", "⌫" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.aspectRatio(1.2f),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
    ) {
        Text(text, fontSize = 24.sp, fontWeight = FontWeight.Medium)
    }
}