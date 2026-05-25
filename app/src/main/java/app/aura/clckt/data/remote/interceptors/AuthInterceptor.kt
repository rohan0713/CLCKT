package app.aura.clckt.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        
        // Add auth headers if needed in the future
        // requestBuilder.addHeader("Authorization", "Bearer token")
        
        return chain.proceed(requestBuilder.build())
    }
}
