package com.citrus.digitalsignage.model.vo
import com.google.gson.annotations.SerializedName



data class SendRequest(
    @SerializedName("DeviceID")
    val deviceID: String,
    @SerializedName("Location")
    val location: String
)