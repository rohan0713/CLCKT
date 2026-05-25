package app.aura.clckt.core

import app.aura.clckt.data.remote.RemoteConfigManager

object PhaseConfig {
    // Dynamic control via Firebase Remote Config.
    // Defaults to false so Phase 1 is the default state.
    val isPhase2Enabled: Boolean
        get() = try {
            RemoteConfigManager.getBoolean("is_phase_2_enabled")
        } catch (e: Exception) {
            false
        }
}
