package com.example.data.repository

import android.content.Context
import android.os.Vibrator
import android.os.VibrationEffect
import android.os.Build
import com.example.data.ble.BleManager
import com.example.data.model.BeaconVibe
import com.example.data.model.NomadBeacon
import com.example.data.model.ProximityWave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

data class MyBeaconProfile(
    val isActive: Boolean = false,
    val handle: String = "Dev Nomad 💻",
    val vibe: BeaconVibe = BeaconVibe.OPEN_TO_CHAT,
    val topic: String = "Indie hacking & offline-first Android apps",
    val tableLocation: String = "Window table 3",
    val autoExpireMinutes: Int = 60,
    val expiresAtTimestamp: Long = 0L,
    val broadcastError: String? = null
) {
    val remainingSeconds: Long
        get() {
            if (!isActive || expiresAtTimestamp <= 0) return 0
            val diff = (expiresAtTimestamp - System.currentTimeMillis()) / 1000
            return diff.coerceAtLeast(0)
        }
}

class BeaconRepository(
    private val context: Context,
    private val bleManager: BleManager = BleManager(context)
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

    private val _myBeacon = MutableStateFlow(MyBeaconProfile())
    val myBeacon: StateFlow<MyBeaconProfile> = _myBeacon.asStateFlow()

    private val _surroundingBeacons = MutableStateFlow<List<NomadBeacon>>(emptyList())
    val surroundingBeacons: StateFlow<List<NomadBeacon>> = _surroundingBeacons.asStateFlow()

    private val _incomingWaves = MutableStateFlow<List<ProximityWave>>(emptyList())
    val incomingWaves: StateFlow<List<ProximityWave>> = _incomingWaves.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // Simulation toggle: enabled by default in emulator/demo environments so user has immediate rich experience
    private val _isSimulationEnabled = MutableStateFlow(true)
    val isSimulationEnabled: StateFlow<Boolean> = _isSimulationEnabled.asStateFlow()

    private var simulationJob: Job? = null
    private var expirationTimerJob: Job? = null

    private val sampleNomads = listOf(
        NomadBeacon(
            id = "nomad_maya_01",
            handle = "Maya 🎨 UI/UX",
            vibe = BeaconVibe.OPEN_TO_CHAT,
            topic = "Designing mobile micro-interactions & looking for best matcha",
            tableLocation = "Corner table by plants",
            rssi = -52,
            estimatedDistanceMeters = 1.2f,
            radarAngleDeg = 45f,
            icebreakers = listOf(
                "What design system do you prefer?",
                "Which city are you heading to next month?",
                "Any good noise-canceling headphone recommendations?"
            )
        ),
        NomadBeacon(
            id = "nomad_kai_02",
            handle = "Kai ☕ Indie Founder",
            vibe = BeaconVibe.COFFEE_RUN,
            topic = "Grabbing a flat white, building a solo SaaS",
            tableLocation = "Espresso bar high-top",
            rssi = -64,
            estimatedDistanceMeters = 2.4f,
            radarAngleDeg = 130f,
            icebreakers = listOf(
                "How's the coffee roast here today?",
                "What's your current tech stack?",
                "Solo founder or with a team?"
            )
        ),
        NomadBeacon(
            id = "nomad_elena_03",
            handle = "Elena 💡 AI Researcher",
            vibe = BeaconVibe.BRAINSTORM,
            topic = "Edge AI models & LLM latency on device",
            tableLocation = "Big oak communal table",
            rssi = -71,
            estimatedDistanceMeters = 3.8f,
            radarAngleDeg = 210f,
            icebreakers = listOf(
                "Running on-device ML or cloud APIs?",
                "How long have you lived the nomad life?",
                "Favorite coworking space so far?"
            )
        ),
        NomadBeacon(
            id = "nomad_lucas_04",
            handle = "Lucas 🤝 Backend & Rust",
            vibe = BeaconVibe.COWORK_CHILL,
            topic = "Distributed databases & crypto privacy",
            tableLocation = "Mezzanine lounge",
            rssi = -78,
            estimatedDistanceMeters = 5.6f,
            radarAngleDeg = 315f,
            icebreakers = listOf(
                "What editor/IDE do you swear by?",
                "Best local food spot within 5 min walk?",
                "Working late or signing off soon?"
            )
        ),
        NomadBeacon(
            id = "nomad_sofia_05",
            handle = "Sofia ♟️ Product & Chess",
            vibe = BeaconVibe.QUICK_GAME,
            topic = "Up for a 5-minute blitz chess game on break",
            tableLocation = "Outdoor garden terrace",
            rssi = -85,
            estimatedDistanceMeters = 8.1f,
            radarAngleDeg = 280f,
            icebreakers = listOf(
                "White or black pieces?",
                "Do you play blitz or rapid?",
                "What got you into product management?"
            )
        )
    )

    init {
        // Start simulation loop so initial radar has realistic nomad hub activity
        startSimulation()
    }

    fun setSimulationMode(enabled: Boolean) {
        _isSimulationEnabled.value = enabled
        if (enabled) {
            startSimulation()
        } else {
            simulationJob?.cancel()
            _surroundingBeacons.value = emptyList()
        }
    }

    private fun startSimulation() {
        simulationJob?.cancel()
        _surroundingBeacons.value = sampleNomads
        simulationJob = coroutineScope.launch {
            while (isActive) {
                delay(2500)
                // Add realistic RF noise / proximity flutter to surrounding beacons
                _surroundingBeacons.update { list ->
                    list.map { beacon ->
                        val rssiDelta = Random.nextInt(-2, 3)
                        val newRssi = (beacon.rssi + rssiDelta).coerceIn(-95, -45)
                        val newDist = bleManager.calculateDistanceMeters(newRssi)
                        beacon.copy(
                            rssi = newRssi,
                            estimatedDistanceMeters = newDist
                        )
                    }
                }
            }
        }
    }

    fun startScanning() {
        _isScanning.value = true
        bleManager.startScanning(
            onBeaconDiscovered = { discovered ->
                _surroundingBeacons.update { current ->
                    val filtered = current.filterNot { it.id == discovered.id }
                    filtered + discovered.copy(
                        radarAngleDeg = Random.nextFloat() * 360f
                    )
                }
            },
            onError = { err ->
                // Keep simulation or fallback
            }
        )
    }

    fun stopScanning() {
        _isScanning.value = false
        bleManager.stopScanning()
    }

    fun updateMyBeacon(
        handle: String,
        vibe: BeaconVibe,
        topic: String,
        tableLocation: String,
        autoExpireMinutes: Int
    ) {
        _myBeacon.update {
            it.copy(
                handle = handle,
                vibe = vibe,
                topic = topic,
                tableLocation = tableLocation,
                autoExpireMinutes = autoExpireMinutes
            )
        }
        if (_myBeacon.value.isActive) {
            broadcastBeacon()
        }
    }

    fun toggleBeaconActive(active: Boolean) {
        if (active) {
            val expireTime = System.currentTimeMillis() + (_myBeacon.value.autoExpireMinutes * 60 * 1000L)
            _myBeacon.update { it.copy(isActive = true, expiresAtTimestamp = expireTime, broadcastError = null) }
            broadcastBeacon()
            startExpirationCountdown()
        } else {
            _myBeacon.update { it.copy(isActive = false, expiresAtTimestamp = 0L) }
            bleManager.stopBroadcasting()
            expirationTimerJob?.cancel()
        }
    }

    private fun broadcastBeacon() {
        val current = _myBeacon.value
        bleManager.startBroadcasting(
            handle = current.handle,
            vibe = current.vibe,
            topic = current.topic,
            tableLocation = current.tableLocation,
            onSuccess = {
                _myBeacon.update { it.copy(broadcastError = null) }
            },
            onError = { err ->
                _myBeacon.update { it.copy(broadcastError = err) }
            }
        )
    }

    private fun startExpirationCountdown() {
        expirationTimerJob?.cancel()
        expirationTimerJob = coroutineScope.launch {
            while (isActive) {
                delay(1000)
                val remaining = _myBeacon.value.remainingSeconds
                if (remaining <= 0) {
                    toggleBeaconActive(false)
                    break
                }
            }
        }
    }

    fun sendProximityWave(targetBeacon: NomadBeacon, messageText: String? = null) {
        triggerHaptic()
        val defaultWave = "Sent a friendly wave from ${_myBeacon.value.tableLocation} 👋"
        val message = messageText ?: defaultWave

        // Mark local interaction
        _surroundingBeacons.update { list ->
            list.map {
                if (it.id == targetBeacon.id) it.copy(isDirectWaveReceived = true) else it
            }
        }

        // Simulate immediate response after a brief realistic moment if simulation is on
        coroutineScope.launch {
            delay(1800)
            val replyWave = ProximityWave(
                id = UUID.randomUUID().toString(),
                senderId = targetBeacon.id,
                senderHandle = targetBeacon.handle,
                senderTable = targetBeacon.tableLocation,
                vibe = targetBeacon.vibe,
                message = "👋 ${targetBeacon.handle} waved back! \"Pull up a chair or say hi when ready!\""
            )
            _incomingWaves.update { listOf(replyWave) + it }
            triggerHaptic()
        }
    }

    fun clearWaves() {
        _incomingWaves.value = emptyList()
    }

    private fun triggerHaptic() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(45)
            }
        } catch (_: Exception) {}
    }
}
