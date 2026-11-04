# 📡 ProxiBeacon — Pure Proximity Signals for Digital Nomads

[![Release](https://img.shields.io/badge/Release-v1.0.00-0000f8.svg?style=flat-square)](https://github.com/proxi-beacon)
[![License: MIT](https://img.shields.io/badge/License-MIT-00c853.svg?style=flat-square)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Web-0000f8.svg?style=flat-square)](#)
[![Bluetooth](https://img.shields.io/badge/Bluetooth-BLE%205.0-00d4ff.svg?style=flat-square)](#)
[![Design](https://img.shields.io/badge/UI-Glassmorphism%20%23f8f8f8%20%26%20%230000f8-0000f8.svg?style=flat-square)](#)

> **Spontaneous, real-time "open to chat" signals for digital nomads, with no profiles, swipes, or algorithms—just proximity.**

ProxiBeacon is an ultra-lightweight, zero-install proximity beacon application designed for digital nomads working in coffee shops, coworking hubs, and airport lounges. Instead of matching with users 50 miles away via cloud algorithms, ProxiBeacon uses **Bluetooth Low Energy (BLE)** to detect who is sitting right in the room with you.

---

## 🌟 Highlights & Features

### 1. 📡 Concentric Proximity Radar
* **Concentric Distance Rings:** Accurately visualizes physical proximity tiers using Bluetooth signal strength (RSSI):
  * **< 1.5m:** Immediate zone (adjacent tables or same communal desk)
  * **1.5m – 4m:** Near zone (nearby tables)
  * **4m – 8m:** Mid-range zone (across the café)
  * **8m – 12m:** Perimeter (outdoor patio or entrance)
* **Rotating Sweep Line & Blips:** Color-coded blips show real-time social vibe status and distance in decibels (`dBm`).
* **Tap-to-Inspect:** Tap any blip on the radar to inspect seating cues and initiate a friendly in-person wave.

### 2. 🟢 Ephemeral "My Beacon" Broadcaster
* **One-Tap Broadcast:** Flip the switch to emit your presence packet without accounts or sign-ups.
* **Social Vibe Presets:**
  * 🟢 **Open to Chat** — Break time, ready to converse & meet new nomads.
  * ☕ **Coffee & Food Run** — Grabbing a flat white or bite; join in!
  * 💡 **Brainstorm & Feedback** — Bouncing indie apps, architecture, or creative ideas.
  * 🤝 **Co-work & Chill** — Working quietly, but a friendly wave is welcome.
  * ♟️ **Quick Game / Stretch** — Down for chess, table tennis, or a walk.
  * 🎯 **Deep Focus** — Headphones on; beacon passive.
* **Physical Seating Cue:** e.g., *"Window high-top table #3, by the monsteras"* — allows people to spot you naturally without GPS tracking.
* **Auto-Expire Countdown:** 30m, 1h, 2h, or 4h timer shuts down the beacon automatically when packing up.

### 3. 💎 Ultra-Lightweight Glassmorphic WebApp
* **Zero Installation Required:** Self-contained, responsive on all devices (mobile, tablet, desktop).
* **Glassmorphic Palette:** Crisp frosted alabaster white (`#f8f8f8`), radiant ambient mesh, and vibrant electric ultramarine (`#0000f8`).
* **Audio Synthesizer:** Web Audio API synthesizes crystal chime notifications when proximity waves are exchanged.

### 4. 🎮 3D Three.js Holographic Scene & "Beacon Blitz" Mini-Game
* **Three.js 3D Engine:** PBR studio lighting (`ACESFilmicToneMapping`), procedural 3-part composite models, and rotating energy rings.
* **Interactive Mini-Game:** Steer your beacon avatar via D-pad or `WASD` to collect coffee beans, connect with nearby nomad beacons, and earn high-score multipliers.

### 5. 🎙️ AI Voice & Transcription (`gemini-3.5-transcribe`)
* **Hands-Free Voice Input:** Speak your conversation hooks or table cues; the audio is transcribed and formatted in real time.

### 6. ✨ Live AI Icebreaker Coach (`gemini-3.8-live`)
* **Context-Aware Starters:** Generates spontaneous, tailored conversation starters to remove the awkwardness of saying hello in person.

### 7. 📍 Google Maps Data Grounding for Nomad Cafés
* **Curated Digital Nomad Hubs:** Verified workspaces in major nomad hubs (Lisbon, Bali, Chiang Mai, etc.) with real WiFi speeds, power outlet density, and 1-tap Google Maps directions.

### 8. 🎬 30-Second In-App Animated Video Demo Reel
* Story-style video player with 5 narrated scenes, progress bars, play/pause controls, and an instant **"Launch Live Café Radar"** button.

---

## 📱 Real-Life Testing with Two Phones (Step-by-Step)

### Method A: Zero-Install Instant WebApp (Fastest — 30 Seconds)
1. Open the shared web URL on both **Phone 1** and **Phone 2**:
   ```
   https://ais-pre-ztrpejdgsm7j3mycbv3y3v-695354829333.asia-east1.run.app
   ```
2. **Phone 1:** Turn the Beacon Switch **ON**, pick 🟢 *Open to Chat*, and set table to *"Window table 1"*.
3. **Phone 2:** Open the Radar, see Phone 1's blip, tap it, and hit **"Send Proximity Wave"**.
4. Both devices chime and exchange the wave immediately!

### Method B: Physical Bluetooth Hardware Testing (Native APK)
1. In AI Studio, open the top-right menu and click **"Download APK"** (or run `gradle assembleDebug` to produce `app/build/outputs/apk/debug/app-debug.apk`).
2. Transfer the APK file to both phones and tap to install.
3. Open **ProxiBeacon** on both phones and grant the Bluetooth permissions.
4. On **Phone 1**: Tap the floating toggle pill to select **"📡 Native Android BLE"**, set your handle, and turn the beacon switch **ON**.
5. On **Phone 2**: Switch to **"📡 Native Android BLE"**. Phone 1 will appear on Phone 2's radar in real time!
6. Walk closer or farther away: watch the signal strength (dBm) and distance estimate dynamically adjust.
7. Tap the blip on Phone 2 and tap **"Send Proximity Wave"** — Phone 1 will physically vibrate with haptic feedback!

---

## 🚀 How to Publish

### Option 1: Google Play Store (Android AAB Release)
1. **Google Play Console Account:** Create an account at [play.google.com/console](https://play.google.com/console).
2. **Build Signed Release Bundle:**
   ```bash
   gradle :app:bundleRelease
   ```
   The `.aab` file will be generated at `app/build/outputs/bundle/release/app-release.aab`.
3. **Upload to Play Console:**
   * Create app listing titled **ProxiBeacon**.
   * Declare permissions (`BLUETOOTH_SCAN`, `BLUETOOTH_ADVERTISE`).
   * Privacy statement: State that the app operates 100% ephemerally via local Bluetooth radio waves and stores no remote user tracking.
   * Submit to Production or Closed Testing.

### Option 2: Free Instant Web App / PWA (Netlify / Vercel / GitHub Pages)
1. Drag and drop the `app/src/main/assets/index.html` file onto [app.netlify.com/drop](https://app.netlify.com/drop) or deploy via GitHub Pages.
2. Share your custom URL (e.g. `https://proxibeacon.netlify.app`).
3. Users on iOS and Android can open the link in any mobile browser, tap **"Add to Home Screen"**, and use it with zero installation required!

---

## 🛠️ Tech Stack & Architecture

* **Android OS:** Kotlin, Jetpack Compose (Material Design 3), MVVM Architecture
* **Hardware BLE Layer:** `BluetoothLeAdvertiser`, `BluetoothLeScanner`, RF Log-Distance Path Loss model
* **Web Frontend:** Standalone HTML5, Glassmorphism CSS (`backdrop-filter: blur(24px)`), Web Audio API
* **3D Engine:** Three.js (WebGL, PBR materials, `ACESFilmicToneMapping`)
* **AI & Grounding:** Gemini 3.5 Transcribe, Gemini 3.8 Live, Google Maps Data Grounding
* **Typography:** Space Grotesk & Plus Jakarta Sans

---

## 🔒 Privacy Guarantee

* ❌ **No account creation or phone numbers required**
* ❌ **No GPS satellite tracking**
* ❌ **No algorithmic feeds, ranking, or shadow-banning**
* ❌ **No persistent cloud chat databases**
* ✅ **100% ephemeral Bluetooth Low Energy radio waves**
* ✅ **Auto-expires when packing up your laptop**

---

## 📄 Release Notes — v1.0.00

* **Initial Production Release (v1.0.00)**
* Real-time BLE Broadcaster & Scanner with RSSI distance estimation.
* Concentric Proximity Radar with rotating sweep beam and interactive blips.
* Lightweight Glassmorphic WebApp engine with `#f8f8f8` and `#0000f8` styling.
* Three.js 3D Holographic Scene & "Beacon Blitz" mini-game.
* Hands-free Audio Transcription (`gemini-3.5-transcribe`) and Live AI Icebreaker Coach (`gemini-3.8-live`).
* Google Maps Nomad Café Grounding.
* 30-Second In-App Animated Video Demo Reel.
* Auto-expire countdown timer ring.

---

## 📜 License

Distributed under the [MIT License](LICENSE). Copyright © 2026 ProxiBeacon Contributors.
