package com.waseefakhtar.invitecustomers.util

import kotlin.math.acos
import kotlin.math.sin

private const val INTERCOM_LAT = 53.339428
private const val INTERCOM_LONG = -6.257664

object DistanceUtil {

    fun getDistance(customerLongitude: Double, customerLatitude: Double): Double {
        val x1 = Math.toRadians(INTERCOM_LONG)
        val y1 = Math.toRadians(INTERCOM_LAT)
        val x2 = Math.toRadians(customerLongitude)
        val y2 = Math.toRadians(customerLatitude)

        // Computing using Law of Cosines
        var angle1 = acos(
            sin(x1) * sin(x2)
                    + Math.cos(x1) * Math.cos(x2) * Math.cos(y1 - y2)
        )

        // convert back to degrees
        // convert back to degrees
        angle1 = Math.toDegrees(angle1)

        // each degree on a great circle of Earth is 60 nautical miles
        // each degree on a great circle of Earth is 60 nautical miles
        val distance1 = 60 * angle1

        println("$distance1 nautical miles")

        val distanceInKm = distance1 * 1.852

        println("$distanceInKm kms")

        return distanceInKm
    }
}