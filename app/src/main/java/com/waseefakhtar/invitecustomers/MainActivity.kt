package com.waseefakhtar.invitecustomers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.waseefakhtar.invitecustomers.data.Customer
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.lang.Math.*
import kotlin.math.acos
import kotlin.math.sin

private const val INTERCOM_LAT = 53.339428
private const val INTERCOM_LONG = -6.257664
private const val DISTANCE_CAP = 100.0


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val customerList = getAllCustomers()

        for (customer in customerList) {
            val distance = getDistance(customer.longitude.toDouble(), customer.latitude.toDouble())

            if (distance <= DISTANCE_CAP) {
                println("${customer.name} is invited! Distance: ${distance}")
            }
        }
    }

    private fun getAllCustomers(): ArrayList<Customer> {
        try {
            val assetManager = assets
            val ims: InputStream = assetManager.open("customers.txt")
            val gson = GsonBuilder().setLenient().create()
            val reader: Reader = InputStreamReader(ims)

            val customersArray = reader.readLines()

            val customerList = arrayListOf<Customer>()
            for (customer in customersArray) {
                val customerElement = gson.fromJson(customer, Customer::class.java)
                customerList.add(customerElement)
            }

            return customerList
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return arrayListOf()
    }

    private fun getDistance(customerLongitude: Double, customerLatitude: Double): Double {
        val x1 = toRadians(INTERCOM_LONG)
        val y1 = toRadians(INTERCOM_LAT)
        val x2 = toRadians(customerLongitude)
        val y2 = toRadians(customerLatitude)

        // Computing using Law of Cosines
        var angle1 = acos(
            sin(x1) * sin(x2)
                    + cos(x1) * cos(x2) * cos(y1 - y2)
        )

        // convert back to degrees
        // convert back to degrees
        angle1 = toDegrees(angle1)

        // each degree on a great circle of Earth is 60 nautical miles
        // each degree on a great circle of Earth is 60 nautical miles
        val distance1 = 60 * angle1

        println("$distance1 nautical miles")

        val distanceInKm = distance1 * 1.852

        println("$distanceInKm kms")

        return distanceInKm
    }
}
