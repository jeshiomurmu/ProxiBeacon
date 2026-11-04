package com.example.data.model

data class NomadBeacon(
    val id: String,
    val handle: String,
    val vibe: BeaconVibe,
    val topic: String,
    val tableLocation: String,
    val rssi: Int,
    val estimatedDistanceMeters: Float,
    val lastSeenTimestamp: Long = System.currentTimeMillis(),
    val isDirectWaveReceived: Boolean = false,
    val icebreakers: List<String> = emptyList(),
    val radarAngleDeg: Float = 0f
) {
    val proximityZone: ProximityZone
        get() = when {
            rssi >= -55 -> ProximityZone.IMMEDIATE
            rssi >= -70 -> ProximityZone.NEAR
            rssi >= -82 -> ProximityZone.MEDIUM
            else -> ProximityZone.FAR
        }
}

enum class ProximityZone(val label: String, val rangeDesc: String) {
    IMMEDIATE("Immediate", "< 1.5m (Same table / adjacent)"),
    NEAR("Near", "1.5m – 4m (Nearby tables)"),
    MEDIUM("Mid-range", "4m – 8m (Across the cafe)"),
    FAR("Perimeter", "8m – 15m (Patio / counter)")
}

data class ProximityWave(
    val id: String,
    val senderId: String,
    val senderHandle: String,
    val senderTable: String,
    val vibe: BeaconVibe,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
