package app.aura.clckt.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TrendingEventApiResponse(
    @SerializedName("places")
    val places: List<PlacesItem?>? = null,

    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("count")
    val count: Int? = null
) : Serializable

data class PlacesItem(
    @SerializedName("image_url")
    val imageUrl: String? = null,

    @SerializedName("timing")
    val timing: Timing? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("location")
    val location: Location? = null,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("checkins")
    val checkins: List<CheckinsItem?>? = null,

    @SerializedName("aura")
    val aura: Aura? = null
) : Serializable

data class CheckinsItem(
    @SerializedName("style_tag")
    val styleTag: String? = null,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("checked_in_at")
    val checkedInAt: String? = null,

    @SerializedName("aura_points")
    val auraPoints: Int? = null
) : Serializable

data class Timing(
    @SerializedName("date")
    val date: String? = null,

    @SerializedName("start_time")
    val startTime: String? = null,

    @SerializedName("display")
    val display: String? = null,

    @SerializedName("end_time")
    val endTime: String? = null,

    @SerializedName("is_live")
    val isLive: Boolean? = null
) : Serializable

data class Location(
    @SerializedName("address")
    val address: String? = null,

    @SerializedName("distance_km")
    val distanceKm: Double? = null,

    @SerializedName("lng")
    val lng: Double? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("lat")
    val lat: Double? = null
) : Serializable

data class Aura(
    @SerializedName("base_points")
    val basePoints: Int? = null,

    @SerializedName("max_points")
    val maxPoints: Int? = null,

    @SerializedName("bonus_reason")
    val bonusReason: String? = null,

    @SerializedName("bonus_multiplier")
    val bonusMultiplier: Double? = null
) : Serializable
