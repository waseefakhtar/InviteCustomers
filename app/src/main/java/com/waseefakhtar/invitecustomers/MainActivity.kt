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

        readGson()
    }

    private fun readGson() {
        try {
            val assetManager = assets
            val ims: InputStream = assetManager.open("customers.txt")
            val gson = GsonBuilder().setLenient().create()
            val reader: Reader = InputStreamReader(ims)

            val newArray = reader.readLines()

            for (json in newArray) {
                val json = gson.fromJson(json, Customer::class.java)
                Log.i("readGson", String.format("json: %s", json))
            }


        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
