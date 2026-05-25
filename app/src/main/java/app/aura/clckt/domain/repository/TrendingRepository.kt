package app.aura.clckt.domain.repository

import app.aura.clckt.data.model.TrendingEventApiResponse

interface TrendingRepository {
    suspend fun getTrendingEvents(): TrendingEventApiResponse?
}
