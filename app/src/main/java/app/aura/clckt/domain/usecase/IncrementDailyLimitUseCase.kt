package app.aura.clckt.domain.usecase

import app.aura.clckt.data.local.LocalLimitManager

class IncrementDailyLimitUseCase(private val limitManager: LocalLimitManager) {
    operator fun invoke() {
        limitManager.incrementUsageCount()
    }
}
