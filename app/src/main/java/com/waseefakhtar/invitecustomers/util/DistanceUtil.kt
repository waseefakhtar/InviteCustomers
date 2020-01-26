package com.waseefakhtar.invitecustomers.util

import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

private const val INTERCOM_LAT = 53.339428
private const val INTERCOM_LONG = -6.257664

object DistanceUtil {

    fun getDistance(customerLongitude: Double, customerLatitude: Double): Double {
        val intercomLong = toRadians(INTERCOM_LONG)
        val intercomLat = toRadians(INTERCOM_LAT)
        val customerLong = toRadians(customerLongitude)
        val customerLat = toRadians(customerLatitude)

        // Computing using Law of Cosines
        var angle = acos(
            sin(intercomLong) * sin(customerLong)
                    + cos(intercomLong) * cos(customerLong) * cos(intercomLat - customerLat)
        )

        angle = toDegrees(angle)

        val distanceInNauticalMiles = 60 * angle

        println("$distanceInNauticalMiles nautical miles")

        val distanceInKm = distanceInNauticalMiles * 1.852

        println("$distanceInKm kms")

        return distanceInKm
    }
}