package net.chariskar.breakthemod.client.utils.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

object SerializableUUIDSerializer : KSerializer<SerializableUUID> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("SerializableUUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: SerializableUUID) {
        encoder.encodeString(value.value.toString())
    }

    override fun deserialize(decoder: Decoder): SerializableUUID {
        return SerializableUUID(UUID.fromString(decoder.decodeString()))
    }
}

@Serializable(with = SerializableUUIDSerializer::class)
data class SerializableUUID(val value: UUID) {
    fun toUUID(): UUID {
        return value
    }
    override fun toString(): String {
        return value.toString()
    }
}