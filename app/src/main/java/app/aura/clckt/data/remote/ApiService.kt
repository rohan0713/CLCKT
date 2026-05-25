package app.aura.clckt.data.remote

import app.aura.clckt.core.ApiEndpoints
import app.aura.clckt.data.model.TrendingEventApiResponse
import retrofit2.http.GET

interface ApiService {
    @GET(ApiEndpoints.GetTrendingEvents)
    suspend fun getTrendingEvents(): TrendingEventApiResponse
}
