package app.aura.clckt.domain.usecase

import app.aura.clckt.data.local.LocalLimitManager

class CheckDailyLimitUseCase(private val limitManager: LocalLimitManager) {
    operator fun invoke(): Boolean {
        return limitManager.canScan()
    }
}
