package com.moseti.sdah.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

data class AppFont(val name: String, val fontFamily: FontFamily)

val availableFonts = listOf(
    AppFont("Sans Serif", FontFamily.SansSerif),
    AppFont("Serif", FontFamily.Serif),
    AppFont("Cursive", FontFamily.Cursive)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontControlSheet(
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    fontFamily: FontFamily,
    onFontFamilyChange: (FontFamily) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // font size control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Font Size", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${fontSize.roundToInt()} sp",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = fontSize,
                onValueChange = onFontSizeChange,
                valueRange = 14f..28f, // min/max font sizes
                steps = 13
            )
            Spacer(Modifier.height(24.dp))

            // font Family Control
            Text("Font Style", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                availableFonts.forEach { appFont ->
                    SegmentedButton(
                        selected = fontFamily == appFont.fontFamily,
                        onClick = { onFontFamilyChange(appFont.fontFamily) },
                        shape = SegmentedButtonDefaults.baseShape
                    ) {
                        Text(appFont.name, fontFamily = appFont.fontFamily)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}