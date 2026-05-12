package app.aura.clckt.data.model

import com.google.gson.annotations.SerializedName

data class TrendingEvent(
    val name: String,
    val location: String,
    val aura: Int,
    @SerializedName(value = "logo_image", alternate = ["image_url", "imageUrl"])
    val imageUrl: String
)