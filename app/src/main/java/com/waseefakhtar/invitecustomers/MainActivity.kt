package com.waseefakhtar.invitecustomers

import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.waseefakhtar.invitecustomers.adapter.CustomerListAdapter
import com.waseefakhtar.invitecustomers.data.Customer
import com.waseefakhtar.invitecustomers.network.ExecuteJSONTask
import com.waseefakhtar.invitecustomers.network.OnPostExecuteListener
import com.waseefakhtar.invitecustomers.util.DistanceUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.Math.*
import kotlin.collections.ArrayList
import kotlin.math.acos
import kotlin.math.sin


private const val DISTANCE_CAP = 100.0
private const val CUSTOMERS_LIST_URL = "https://s3.amazonaws.com/intercom-take-home-test/customers.txt"


class MainActivity : AppCompatActivity(), MediaScannerConnection.OnScanCompletedListener, OnPostExecuteListener {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var customerListAdapter: CustomerListAdapter
    private var customerList: ArrayList<Customer> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        ExecuteJSONTask(this).execute(CUSTOMERS_LIST_URL)
    }

    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(false)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        customerListAdapter = CustomerListAdapter(customerList)
        recyclerView.adapter = customerListAdapter
    }

    override fun onPostExecute(customerList: ArrayList<Customer>?) {
        customerList?.let {

            val invitedCustomers = mutableMapOf<Int, String>()

            for (customer in it) {
                val distance = DistanceUtil.getDistance(customer.longitude.toDouble(), customer.latitude.toDouble())

                if (distance <= DISTANCE_CAP) {
                    println("${customer.name} is invited! Distance: ${distance}")
                    invitedCustomers[customer.userId] = customer.name
                }
            }

            saveResult(invitedCustomers.toSortedMap())

            this.customerList = it
            initRecyclerView()
        }
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
