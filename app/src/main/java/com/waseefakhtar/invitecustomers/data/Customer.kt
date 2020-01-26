package com.waseefakhtar.invitecustomers.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class Customer(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val _latitude: String,
    @SerializedName("longitude") val _longitude: String
) {
    val longitude
        get() = _longitude.toDouble()

    val latitude
        get() = _latitude.toDouble()
}


