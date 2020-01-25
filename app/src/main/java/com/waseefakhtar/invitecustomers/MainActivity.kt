package com.waseefakhtar.invitecustomers

import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.waseefakhtar.invitecustomers.data.Customer
import com.waseefakhtar.invitecustomers.network.ExecuteJSONTask
import com.waseefakhtar.invitecustomers.network.OnPostExecuteListener
import java.io.*
import java.lang.Math.*
import kotlin.collections.ArrayList
import kotlin.math.acos
import kotlin.math.sin


private const val INTERCOM_LAT = 53.339428
private const val INTERCOM_LONG = -6.257664
private const val DISTANCE_CAP = 100.0
private const val CUSTOMERS_LIST_URL = "https://s3.amazonaws.com/intercom-take-home-test/customers.txt"


class MainActivity : AppCompatActivity(), MediaScannerConnection.OnScanCompletedListener, OnPostExecuteListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ExecuteJSONTask(this).execute(CUSTOMERS_LIST_URL)
    }

    override fun onPostExecute(customerList: ArrayList<Customer>?) {
        customerList?.let {
            val invitedCustomers = mutableMapOf<Int, String>()

            for (customer in it) {
                val distance = getDistance(customer.longitude.toDouble(), customer.latitude.toDouble())


                if (distance <= DISTANCE_CAP) {
                    println("${customer.name} is invited! Distance: ${distance}")
                    invitedCustomers[customer.userId] = customer.name
                }
            }

            saveResult(invitedCustomers.toSortedMap())
        }
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

    private fun saveResult(invitedCustomers: Map<Int, String>) {
        val directoryPath = Environment.getExternalStoragePublicDirectory("Intercom Party Invitations")

        if (!directoryPath.exists()) {
            directoryPath.mkdir()
        }

        val documentTitle = "output.txt"
        val filePath = File(directoryPath, documentTitle)

        MediaScannerConnection.scanFile(this, arrayOf(filePath.toString()), null, this)

        try {
            val writer = FileWriter(filePath)
            invitedCustomers.forEach { writer.append("UserID: ${it.key} Customer Name: ${it.value}\n") }
            writer.flush()
            writer.close()

            Toast.makeText(this, "Text File successfully saved at location: ${directoryPath}!", Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            Log.e("main", "error $e");
            Toast.makeText(this, "Something went wrong: $e",  Toast.LENGTH_LONG).show();
        }
    }

    override fun onScanCompleted(p0: String?, p1: Uri?) {

    }
}
