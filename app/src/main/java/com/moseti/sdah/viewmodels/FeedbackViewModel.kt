package com.moseti.sdah.viewmodels

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FeedbackUiState(
    val uiRating: Float = 8f,
    val functionalityRating: Float = 8f,
    val performanceRating: Float = 8f,
    val positiveFeedback: String = "",
    val problemsEncountered: String = "",
    val suggestions: String = "",
    val wantsUpdates: Boolean = false,
    val userEmail: String = ""
)

class FeedbackViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiRatingChanged(rating: Float) {
        _uiState.update { it.copy(uiRating = rating) }
    }

    fun onFunctionalityRatingChanged(rating: Float) {
        _uiState.update { it.copy(functionalityRating = rating) }
    }

    fun onPerformanceRatingChanged(rating: Float) {
        _uiState.update { it.copy(performanceRating = rating) }
    }

    fun onPositiveFeedbackChanged(feedback: String) {
        _uiState.update { it.copy(positiveFeedback = feedback) }
    }

    fun onProblemsEncounteredChanged(problems: String) {
        _uiState.update { it.copy(problemsEncountered = problems) }
    }

    fun onSuggestionsChanged(suggestions: String) {
        _uiState.update { it.copy(suggestions = suggestions) }
    }

    fun onWantsUpdatesChanged(wantsUpdates: Boolean) {
        _uiState.update { it.copy(wantsUpdates = wantsUpdates) }
    }

    fun onUserEmailChanged(email: String) {
        _uiState.update { it.copy(userEmail = email) }
    }

    fun submitFeedback(context: Context) {
        val currentState = _uiState.value

        val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
        val androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        val appVersion = "0.0.1" // Todo Get from BuildConfig

        val body = """
            --- USER RATINGS ---
            UI/Design: ${currentState.uiRating.toInt()}/10
            Functionality: ${currentState.functionalityRating.toInt()}/10
            Performance: ${currentState.performanceRating.toInt()}/10
            
            --- QUALITATIVE FEEDBACK ---
            What I liked most:
            ${currentState.positiveFeedback}
            
            Problems Encountered:
            ${currentState.problemsEncountered}
            
            Suggestions for new features:
            ${currentState.suggestions}
            
            --- USER INFO ---
            Wants Updates: ${if (currentState.wantsUpdates) "Yes" else "No"}
            User Email: ${if (currentState.wantsUpdates && currentState.userEmail.isNotBlank()) currentState.userEmail else "Not provided"}
            
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
            Toast.makeText(context, "Thank you!", Toast.LENGTH_LONG).show()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
        }
    }
}