package app.aura.clckt.data.remote

import app.aura.clckt.BuildConfig
import app.aura.clckt.core.ApiEndpoints
import app.aura.clckt.data.model.TrendingEvent
import app.aura.clckt.data.remote.interceptors.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface ApiService {
    @GET(ApiEndpoints.getTrendingEvents)
    suspend fun getTrendingEvents(): app.aura.clckt.data.model.TrendingEventApiResponse
}

object ApiClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Use BODY level for development, and NONE or BASIC for production
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    suspend fun getTrendingEvents(): List<TrendingEvent> {
        return try {
            val response = apiService.getTrendingEvents()
            response.places?.mapNotNull { place ->
                if (place != null) {
                    TrendingEvent(
                        name = place.name ?: "Unknown",
                        location = place.location?.address ?: place.location?.city ?: "Unknown Location",
                        aura = place.aura?.basePoints ?: 0,
                        imageUrl = place.imageUrl ?: ""
                    )
                } else null
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
