package com.moseti.sdah.models

import kotlinx.serialization.Serializable

@Serializable
data class Hymn(
    val number: Int,
    val title: String,
    val verses: List<Verse>,
    val chorus: Chorus?
)

@Serializable
data class Verse(
    val number: Int,
    val lines: List<String>
)

@Serializable
data class Chorus(
    val lines: List<String>
)