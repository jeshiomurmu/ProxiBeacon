package com.example.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import com.example.data.model.BeaconVibe
import com.example.data.model.NomadBeacon
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.math.pow

class BleManager(private val context: Context) {

    companion object {
        private const val TAG = "ProxiBleManager"
        val PROXIBEACON_UUID: UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        val PARCEL_UUID = ParcelUuid(PROXIBEACON_UUID)
        private const val PROTOCOL_HEADER: Byte = 0x50 // 'P'
    }

    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private var advertiser: BluetoothLeAdvertiser? = null
    private var scanner: BluetoothLeScanner? = null

    private var currentAdvertiseCallback: AdvertiseCallback? = null
    private var currentScanCallback: ScanCallback? = null

    val isBluetoothSupported: Boolean
        get() = bluetoothAdapter != null

    val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    val isAdvertisingSupported: Boolean
        get() = bluetoothAdapter?.isMultipleAdvertisementSupported == true

    @SuppressLint("MissingPermission")
    fun startBroadcasting(
        handle: String,
        vibe: BeaconVibe,
        topic: String,
        tableLocation: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isBluetoothSupported || !isBluetoothEnabled) {
            onError("Bluetooth is not enabled or supported on this device.")
            return
        }

        advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        if (advertiser == null) {
            onError("BLE Peripheral / Advertising is not supported by this hardware.")
            return
        }

        // Stop any existing broadcast
        stopBroadcasting()

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .setTimeout(0)
            .build()

        // Encode compact payload (Protocol 1 byte + vibe code 1 byte + text)
        val handleBytes = handle.take(12).toByteArray(StandardCharsets.UTF_8)
        val payload = ByteArray(2 + handleBytes.size)
        payload[0] = PROTOCOL_HEADER
        payload[1] = vibe.code
        System.arraycopy(handleBytes, 0, payload, 2, handleBytes.size)

        val advertiseData = AdvertiseData.Builder()
            .addServiceUuid(PARCEL_UUID)
            .addServiceData(PARCEL_UUID, payload)
            .setIncludeDeviceName(false)
            .setIncludeTxPowerLevel(false)
            .build()

        currentAdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                super.onStartSuccess(settingsInEffect)
                Log.d(TAG, "BLE advertising started successfully")
                onSuccess()
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                val reason = when (errorCode) {
                    ADVERTISE_FAILED_ALREADY_STARTED -> "Already started"
                    ADVERTISE_FAILED_DATA_TOO_LARGE -> "Data packet too large"
                    ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> "BLE advertising unsupported"
                    ADVERTISE_FAILED_INTERNAL_ERROR -> "Internal hardware error"
                    ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> "Too many active BLE advertisers"
                    else -> "Error code: $errorCode"
                }
                Log.w(TAG, "BLE advertising failed: $reason")
                onError(reason)
            }
        }

        try {
            advertiser?.startAdvertising(settings, advertiseData, currentAdvertiseCallback)
        } catch (e: SecurityException) {
            onError("Bluetooth permissions are missing.")
        } catch (e: Exception) {
            onError(e.message ?: "Failed to start BLE broadcast.")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopBroadcasting() {
        try {
            currentAdvertiseCallback?.let {
                advertiser?.stopAdvertising(it)
                currentAdvertiseCallback = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping advertising: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun startScanning(
        onBeaconDiscovered: (NomadBeacon) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isBluetoothSupported || !isBluetoothEnabled) {
            onError("Bluetooth is disabled.")
            return
        }

        scanner = bluetoothAdapter?.bluetoothLeScanner
        if (scanner == null) {
            onError("BLE Scanner is unavailable on this device.")
            return
        }

        stopScanning()

        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(PARCEL_UUID)
            .build()

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(0)
            .build()

        currentScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                result?.let { res ->
                    parseScanResult(res)?.let { beacon ->
                        onBeaconDiscovered(beacon)
                    }
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                results?.forEach { res ->
                    parseScanResult(res)?.let { beacon ->
                        onBeaconDiscovered(beacon)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.w(TAG, "BLE Scan failed: $errorCode")
                onError("Scan failed code $errorCode")
            }
        }

        try {
            scanner?.startScan(listOf(scanFilter), scanSettings, currentScanCallback)
        } catch (e: SecurityException) {
            onError("Bluetooth scan permission missing.")
        } catch (e: Exception) {
            onError(e.message ?: "Failed to start BLE scan.")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        try {
            currentScanCallback?.let {
                scanner?.stopScan(it)
                currentScanCallback = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping BLE scan: ${e.message}")
        }
    }

    private fun parseScanResult(result: ScanResult): NomadBeacon? {
        val record = result.scanRecord ?: return null
        val serviceData = record.getServiceData(PARCEL_UUID) ?: return null
        if (serviceData.isEmpty() || serviceData[0] != PROTOCOL_HEADER) return null

        val vibeCode = if (serviceData.size > 1) serviceData[1] else 1
        val vibe = BeaconVibe.fromCode(vibeCode)
        val handle = if (serviceData.size > 2) {
            String(serviceData, 2, serviceData.size - 2, StandardCharsets.UTF_8).trim()
        } else {
            "Nearby Nomad"
        }

        val rssi = result.rssi
        val distance = calculateDistanceMeters(rssi)
        val id = result.device.address ?: UUID.randomUUID().toString()

        return NomadBeacon(
            id = id,
            handle = handle.ifBlank { "Nearby Nomad" },
            vibe = vibe,
            topic = "Open to coffee & conversation",
            tableLocation = "Nearby",
            rssi = rssi,
            estimatedDistanceMeters = distance,
            lastSeenTimestamp = System.currentTimeMillis()
        )
    }

    /**
     * Approximate distance calculation using log-distance path loss model:
     * RSSI = -10 * n * log10(d) + A
     * where A is measured power at 1 meter (approx -59 dBm) and n is path loss exponent (approx 2.4).
     */
    fun calculateDistanceMeters(rssi: Int, measuredPowerAt1m: Int = -59): Float {
        if (rssi == 0) return -1.0f
        val ratio = (measuredPowerAt1m - rssi) / (10.0 * 2.4)
        val distance = 10.0.pow(ratio).toFloat()
        return (distance * 10).toInt() / 10f // round to 1 decimal
    }
}
