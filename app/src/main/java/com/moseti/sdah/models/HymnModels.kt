package com.moseti.sdah.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Hymn(
    val number: Int,
    val title: String,
    val verses: List<Verse>,
    val chorus: Chorus?
)

@Immutable
@Serializable
data class Verse(
    val number: Int,
    val lines: List<String>
)

@Immutable
@Serializable
data class Chorus(
    val lines: List<String>
)