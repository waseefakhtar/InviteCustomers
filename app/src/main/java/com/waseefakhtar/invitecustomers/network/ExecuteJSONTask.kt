package com.waseefakhtar.invitecustomers.network

import android.os.AsyncTask
import com.google.gson.GsonBuilder
import com.waseefakhtar.invitecustomers.data.Customer
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL

class ExecuteJSONTask constructor(private val listener: OnPostExecuteListener) : AsyncTask<String, Void, ArrayList<Customer>>() {

    override fun doInBackground(vararg jsonURL: String?): ArrayList<Customer> {
        jsonURL[0]?.let {
            return getAllCustomers(it)
        } ?: kotlin.run {
            return arrayListOf()
        }
    }

    private fun getAllCustomers(jsonURL: String): java.util.ArrayList<Customer> {
        try {
            val gson = GsonBuilder().setLenient().create()

            val url =  URL(jsonURL)
            val reader: Reader = InputStreamReader(url.openStream())

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

    override fun onPostExecute(result: ArrayList<Customer>?) {
        super.onPostExecute(result)
        listener.onPostExecute(result)
    }
}

interface OnPostExecuteListener {
    fun onPostExecute(result: ArrayList<Customer>?)
}