package com.moseti.sdah.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Immutable
@Serializable
data class Hymn(
    val number: Int,
    val title: String,
    @Serializable(with = ImmutableVerseListSerializer::class)
    val verses: ImmutableList<Verse>,
    val chorus: Chorus?
)

@Immutable
@Serializable
data class Verse(
    val number: Int,
    @Serializable(with = ImmutableStringListSerializer::class)
    val lines: ImmutableList<String>
)

@Immutable
@Serializable
data class Chorus(
    @Serializable(with = ImmutableStringListSerializer::class)
    val lines: ImmutableList<String>
)

// custom serializer for a list of Verses
object ImmutableVerseListSerializer : KSerializer<ImmutableList<Verse>> {
    // We delegate the actual work to the standard ListSerializer
    private val listSerializer = ListSerializer(Verse.serializer())

    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: ImmutableList<Verse>) {
        listSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): ImmutableList<Verse> {
        // Decode as a regular list, then convert to an immutable one
        return listSerializer.deserialize(decoder).toImmutableList()
    }
}

// custom serializer for a list of Strings
object ImmutableStringListSerializer : KSerializer<ImmutableList<String>> {
    // We delegate the actual work to the standard ListSerializer
    private val listSerializer = ListSerializer(String.serializer())

    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: ImmutableList<String>) {
        listSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): ImmutableList<String> {
        // Decode as a regular list, then convert to an immutable one
        return listSerializer.deserialize(decoder).toImmutableList()
    }
}