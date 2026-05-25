package app.aura.clckt.data.repository

import app.aura.clckt.data.model.TrendingEventApiResponse
import app.aura.clckt.data.remote.ApiService
import app.aura.clckt.domain.repository.TrendingRepository

class TrendingRepositoryImpl(
    private val apiService: ApiService
) : TrendingRepository {
    override suspend fun getTrendingEvents(): TrendingEventApiResponse? {
        return try {
            apiService.getTrendingEvents()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
