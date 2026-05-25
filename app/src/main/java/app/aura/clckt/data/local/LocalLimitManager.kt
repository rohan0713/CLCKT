package app.aura.clckt.data.local

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocalLimitManager(context: Context) {
    private val prefs = context.getSharedPreferences("clckt_limit_prefs", Context.MODE_PRIVATE)

    private fun getCurrentDateKey(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getUsageCount(): Int {
        val key = "usage_${getCurrentDateKey()}"
        return prefs.getInt(key, 0)
    }

    fun incrementUsageCount() {
        val key = "usage_${getCurrentDateKey()}"
        val currentCount = getUsageCount()
        prefs.edit().putInt(key, currentCount + 1).apply()
    }

    fun canScan(): Boolean {
        return getUsageCount() < 5
    }
}
