package com.waseefakhtar.invitecustomers

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.waseefakhtar.invitecustomers.data.Customer
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val customerList = getAllCustomers()


        
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
}
