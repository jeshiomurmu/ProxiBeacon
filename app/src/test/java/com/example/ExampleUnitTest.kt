package com.example

import com.example.data.model.BeaconVibe
import com.example.data.model.NomadBeacon
import com.example.data.model.ProximityZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `beacon vibe codes match and map correctly`() {
    val openVibe = BeaconVibe.fromCode(1)
    assertEquals(BeaconVibe.OPEN_TO_CHAT, openVibe)
    assertEquals("🟢", openVibe.emoji)

    val coffeeVibe = BeaconVibe.fromCode(2)
    assertEquals(BeaconVibe.COFFEE_RUN, coffeeVibe)
    assertEquals("☕", coffeeVibe.emoji)

    val brainstormVibe = BeaconVibe.fromCode(3)
    assertEquals(BeaconVibe.BRAINSTORM, brainstormVibe)

    val defaultFallback = BeaconVibe.fromCode(99)
    assertEquals(BeaconVibe.OPEN_TO_CHAT, defaultFallback)
  }

  @Test
  fun `proximity zones calculate properly based on rssi`() {
    val immediateBeacon = NomadBeacon(
      id = "1",
      handle = "Alex",
      vibe = BeaconVibe.OPEN_TO_CHAT,
      topic = "Kotlin",
      tableLocation = "T1",
      rssi = -50,
      estimatedDistanceMeters = 0.8f
    )
    assertEquals(ProximityZone.IMMEDIATE, immediateBeacon.proximityZone)

    val nearBeacon = immediateBeacon.copy(rssi = -65)
    assertEquals(ProximityZone.NEAR, nearBeacon.proximityZone)

    val midBeacon = immediateBeacon.copy(rssi = -75)
    assertEquals(ProximityZone.MEDIUM, midBeacon.proximityZone)

    val farBeacon = immediateBeacon.copy(rssi = -90)
    assertEquals(ProximityZone.FAR, farBeacon.proximityZone)
  }

  @Test
  fun `rssi path loss model produces sensible proximity distance`() {
    val rssi = -55
    val measuredPowerAt1m = -59
    val ratio = (measuredPowerAt1m - rssi) / (10.0 * 2.4)
    val distance = 10.0.pow(ratio).toFloat()
    assertTrue("Distance for -55 dBm should be under 1.5m", distance in 0.5f..1.5f)
  }
}
