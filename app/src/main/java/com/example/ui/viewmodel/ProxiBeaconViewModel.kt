package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BeaconVibe
import com.example.data.model.NomadBeacon
import com.example.data.model.ProximityWave
import com.example.data.repository.BeaconRepository
import com.example.data.repository.MyBeaconProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class ScreenTab {
    RADAR,
    SURROUNDING_LIST,
    WAVES_LOG
}

data class ProxiUiState(
    val myBeacon: MyBeaconProfile = MyBeaconProfile(),
    val surroundingBeacons: List<NomadBeacon> = emptyList(),
    val filteredBeacons: List<NomadBeacon> = emptyList(),
    val selectedBeacon: NomadBeacon? = null,
    val isBroadcastSheetOpen: Boolean = false,
    val isWaveModalOpen: Boolean = false,
    val isScanning: Boolean = false,
    val isSimulationEnabled: Boolean = true,
    val incomingWaves: List<ProximityWave> = emptyList(),
    val filterVibe: BeaconVibe? = null,
    val maxDistanceFilterMeters: Float = 15f,
    val activeTab: ScreenTab = ScreenTab.RADAR,
    val hasUnreadWaves: Boolean = false
)

class ProxiBeaconViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BeaconRepository(application.applicationContext)

    private val _selectedBeacon = MutableStateFlow<NomadBeacon?>(null)
    private val _isBroadcastSheetOpen = MutableStateFlow(false)
    private val _isWaveModalOpen = MutableStateFlow(false)
    private val _filterVibe = MutableStateFlow<BeaconVibe?>(null)
    private val _maxDistanceFilter = MutableStateFlow(15f)
    private val _activeTab = MutableStateFlow(ScreenTab.RADAR)

    val uiState: StateFlow<ProxiUiState> = combine(
        repository.myBeacon,
        repository.surroundingBeacons,
        repository.incomingWaves,
        repository.isScanning,
        repository.isSimulationEnabled,
        _selectedBeacon,
        _filterVibe,
        _maxDistanceFilter,
        _activeTab
    ) { params ->
        val myBeacon = params[0] as MyBeaconProfile
        val allBeacons = params[1] as List<NomadBeacon>
        val waves = params[2] as List<ProximityWave>
        val isScanning = params[3] as Boolean
        val isSimEnabled = params[4] as Boolean
        val selected = params[5] as NomadBeacon?
        val vibeFilter = params[6] as BeaconVibe?
        val maxDist = params[7] as Float
        val tab = params[8] as ScreenTab

        val filtered = allBeacons.filter { beacon ->
            (vibeFilter == null || beacon.vibe == vibeFilter) &&
            (beacon.estimatedDistanceMeters <= maxDist)
        }

        ProxiUiState(
            myBeacon = myBeacon,
            surroundingBeacons = allBeacons,
            filteredBeacons = filtered,
            selectedBeacon = selected,
            isBroadcastSheetOpen = _isBroadcastSheetOpen.value,
            isWaveModalOpen = _isWaveModalOpen.value,
            isScanning = isScanning,
            isSimulationEnabled = isSimEnabled,
            incomingWaves = waves,
            filterVibe = vibeFilter,
            maxDistanceFilterMeters = maxDist,
            activeTab = tab,
            hasUnreadWaves = waves.isNotEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProxiUiState()
    )

    fun toggleBeaconActive(active: Boolean) {
        repository.toggleBeaconActive(active)
    }

    fun updateMyBeacon(
        handle: String,
        vibe: BeaconVibe,
        topic: String,
        tableLocation: String,
        autoExpireMinutes: Int
    ) {
        repository.updateMyBeacon(handle, vibe, topic, tableLocation, autoExpireMinutes)
        _isBroadcastSheetOpen.value = false
    }

    fun openBroadcastSheet() {
        _isBroadcastSheetOpen.value = true
    }

    fun closeBroadcastSheet() {
        _isBroadcastSheetOpen.value = false
    }

    fun selectBeacon(beacon: NomadBeacon?) {
        _selectedBeacon.value = beacon
    }

    fun openWaveModal(beacon: NomadBeacon) {
        _selectedBeacon.value = beacon
        _isWaveModalOpen.value = true
    }

    fun closeWaveModal() {
        _isWaveModalOpen.value = false
    }

    fun sendWave(target: NomadBeacon, messageText: String? = null) {
        repository.sendProximityWave(target, messageText)
        _isWaveModalOpen.value = false
    }

    fun setSimulationMode(enabled: Boolean) {
        repository.setSimulationMode(enabled)
    }

    fun setFilterVibe(vibe: BeaconVibe?) {
        _filterVibe.value = vibe
    }

    fun setMaxDistanceFilter(meters: Float) {
        _maxDistanceFilter.value = meters
    }

    fun setActiveTab(tab: ScreenTab) {
        _activeTab.value = tab
    }

    fun startScanning() {
        repository.startScanning()
    }

    fun stopScanning() {
        repository.stopScanning()
    }

    fun clearWaves() {
        repository.clearWaves()
    }
}
