package net.chariskar.breakthemod.client.api.types

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.chariskar.breakthemod.client.utils.serialization.SerializableUUID

@Serializable
data class StaffList(
    @Contextual val owner: List<SerializableUUID>,
    @Contextual val admin: List<SerializableUUID>,
    @Contextual val developer: List<SerializableUUID>,
    @Contextual val moderator: List<SerializableUUID>,
    @Contextual val helper: List<SerializableUUID>
) {
    fun allStaff(): List<SerializableUUID> {
        return (owner + admin + moderator + helper + developer).distinct()
    }
}
