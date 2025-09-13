package com.moseti.sdah.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moseti.sdah.viewmodels.FeedbackViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    onNavigateBack: () -> Unit,
    viewModel: FeedbackViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Your Feedback") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Your feedback helps us improve the app.", style = MaterialTheme.typography.titleLarge)

            HorizontalDivider(Modifier.padding(vertical = 24.dp))
            Text("Rate Your Experience (1-10)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            RatingSlider(
                label = "App UI / Design",
                value = uiState.uiRating,
                onValueChange = viewModel::onUiRatingChanged // Use function reference
            )
            RatingSlider(
                label = "App Functionality",
                value = uiState.functionalityRating,
                onValueChange = viewModel::onFunctionalityRatingChanged
            )
            RatingSlider(
                label = "App Performance",
                value = uiState.performanceRating,
                onValueChange = viewModel::onPerformanceRatingChanged
            )

            HorizontalDivider(Modifier.padding(vertical = 24.dp))
            Text("Tell Us More", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.positiveFeedback,
                onValueChange = viewModel::onPositiveFeedbackChanged,
                label = { Text("What did you like most about the app?") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.problemsEncountered,
                onValueChange = viewModel::onProblemsEncounteredChanged,
                label = { Text("Please describe any issues you encountered") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.suggestions,
                onValueChange = viewModel::onSuggestionsChanged,
                label = { Text("What new features would you like to see?") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
            )

            HorizontalDivider(Modifier.padding(vertical = 24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.wantsUpdates,
                    onCheckedChange = viewModel::onWantsUpdatesChanged
                )
                Text("Receive email updates about new versions", modifier = Modifier.padding(start = 8.dp))
            }

            if (uiState.wantsUpdates) {
                OutlinedTextField(
                    value = uiState.userEmail,
                    onValueChange = viewModel::onUserEmailChanged,
                    label = { Text("Your Email Address") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(onClick = {
                viewModel.submitFeedback(context)
                onNavigateBack() // navigate back after submitting
            },
                modifier = Modifier.fillMaxWidth().height(50.dp).align(Alignment.CenterHorizontally)
            ) {
                Text("Submit Feedback")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RatingSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = value.roundToInt().toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..10f,
            steps = 8
        )
    }
}

private fun sendFeedbackEmail(
    context: Context,
    uiRating: Int,
    functionalityRating: Int,
    performanceRating: Int,
    positiveFeedback: String,
    problems: String,
    suggestions: String,
    wantsUpdates: Boolean,
    email: String
) {
    val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
    val androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    val appVersion = "1.0.0" // Todo Get this from BuildConfig later

    val body = """
        --- USER RATINGS ---
        UI/Design: $uiRating/10
        Functionality: $functionalityRating/10
        Performance: $performanceRating/10
        
        --- QUALITATIVE FEEDBACK ---
        What I liked most:
        $positiveFeedback
        
        Problems Encountered:
        $problems
        
        Suggestions for new features:
        $suggestions
        
        --- USER INFO ---
        Wants Updates: ${if (wantsUpdates) "Yes" else "No"}
        User Email: ${if (wantsUpdates && email.isNotBlank()) email else "Not provided"}
        
        --- DEVICE INFORMATION (Auto-generated) ---
        Device: $deviceModel
        Android Version: $androidVersion
        App Version: $appVersion
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("mosetioba01@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "SDAH App Feedback - v$appVersion")
        putExtra(Intent.EXTRA_TEXT, body)
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
    }
}