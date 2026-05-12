package app.aura.clckt.data.remote

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import app.aura.clckt.data.model.NearbyEvent
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONArray
import app.aura.clckt.data.model.TrendingEvent

object RemoteConfigManager {

    private val remoteConfig = Firebase.remoteConfig

    val isConfigLoaded = mutableStateOf(false)

    fun initConfig(onComplete: () -> Unit = {}) {
        val options = FirebaseApp.getInstance().options
        Log.d("RemoteConfig", "Connected to Project ID: ${options.projectId}")
        Log.d("RemoteConfig", "Connected with App ID: ${options.applicationId}")

        val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }

        // Wait for settings to be applied before proceeding
        remoteConfig.setConfigSettingsAsync(configSettings).addOnCompleteListener {
            // Setup real-time listener
            remoteConfig.addOnConfigUpdateListener(
                    object : com.google.firebase.remoteconfig.ConfigUpdateListener {
                        override fun onUpdate(
                                configUpdate: com.google.firebase.remoteconfig.ConfigUpdate
                        ) {
                            Log.d(
                                    "RemoteConfig",
                                    "Config updated in real-time! Keys: ${configUpdate.updatedKeys}"
                            )
                            remoteConfig.activate().addOnCompleteListener {
                                isConfigLoaded.value =
                                        !isConfigLoaded.value // Toggle to trigger UI refresh
                            }
                        }

                        override fun onError(
                                error:
                                        com.google.firebase.remoteconfig.FirebaseRemoteConfigException
                        ) {
                            Log.e("RemoteConfig", "Real-time update error", error)
                        }
                    }
            )

            // Set defaults
            val defaultValues =
                    mapOf(
                            "user_name" to "John",
                            "welcome_message" to "Hey, ",
                            "event_name" to "Neon Noir Night",
                            "logo_image" to "https://images.unsplash.com/photo-1614850523296-d8c1af93d400?q=80&w=2070&auto=format&fit=crop",
                            "trending_list" to """
                                [
                                  {
                                    "name": "Neon Noir Night",
                                    "location": "Hauz Khas Village",
                                    "aura": 80
                                  },
                                  {
                                    "name": "Jazz at The Piano",
                                    "location": "Gurgaon Sector 15",
                                    "aura": 120
                                  }
                                ]
                            """.trimIndent(),
                            "nearby_list" to """
                                [
                                  {
                                    "name": "Retro Rewind Fest",
                                    "location": "Hauz Khas Village",
                                    "aura": 120,
                                    "distance": "1.2km"
                                  },
                                  {
                                    "name": "Art in the Park",
                                    "location": "Central Park",
                                    "aura": 50,
                                    "distance": "2.5km"
                                  }
                                ]
                            """.trimIndent()
                    )
            remoteConfig.setDefaultsAsync(defaultValues).addOnCompleteListener { onComplete() }
        }
    }

    fun getTrendingList(): List<TrendingEvent> {
        val jsonString = remoteConfig.getString("trending_list")
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<TrendingEvent>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    TrendingEvent(
                        name = obj.getString("name"),
                        location = obj.getString("location"),
                        aura = obj.getInt("aura"),
                        imageUrl = obj.getString("logo_image")
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e("RemoteConfig", "Error parsing trending list", e)
            emptyList()
        }
    }

    fun getNearByItems(): List<NearbyEvent> {
        val jsonString = remoteConfig.getString("nearby_list")
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<NearbyEvent>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    NearbyEvent(
                        name = obj.getString("name"),
                        location = obj.getString("location"),
                        aura = obj.getInt("aura"),
                        distance = obj.getString("distance"),
                        imageUrl = obj.getString("image_url")
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e("RemoteConfig", "Error parsing nearby list", e)
            emptyList()
        }
    }

    fun fetchAndActivate(onComplete: (Boolean) -> Unit = {}) {
        Log.d("RemoteConfig", "Manual fetch started...")
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updated = task.result
                val value = remoteConfig.getString("event_name")
                val source = remoteConfig.getValue("event_name").source

                Log.d("RemoteConfig", "Fetch successful. New config activated: $updated")
                Log.d("RemoteConfig", "event_name value: $value")
                Log.d("RemoteConfig", "Value source: $source (1=Default, 2=Remote, 3=Static)")
                Log.d("RemoteConfig", "All keys: ${remoteConfig.all.keys}")
            } else {
                Log.e("RemoteConfig", "Fetch failed: ${task.exception?.message}")
            }
            isConfigLoaded.value = !isConfigLoaded.value // Toggle to trigger UI refresh
            onComplete(task.isSuccessful)
        }
    }

    fun getString(key: String): String {
        return remoteConfig.getString(key)
    }

    fun getBoolean(key: String): Boolean {
        return remoteConfig.getBoolean(key)
    }
}
