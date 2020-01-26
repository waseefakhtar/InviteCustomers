package com.waseefakhtar.invitecustomers

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.waseefakhtar.invitecustomers.adapter.CustomerListAdapter
import com.waseefakhtar.invitecustomers.data.Customer
import com.waseefakhtar.invitecustomers.network.ExecuteJSONTask
import com.waseefakhtar.invitecustomers.network.OnPostExecuteListener
import com.waseefakhtar.invitecustomers.util.DistanceUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileWriter
import java.io.IOException


private const val DISTANCE_CAP = 100.0
private const val CUSTOMERS_LIST_URL = "https://s3.amazonaws.com/intercom-take-home-test/customers.txt"

private const val REQUEST_CODE_PERMISSIONS = 0
private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)


class MainActivity : AppCompatActivity(), MediaScannerConnection.OnScanCompletedListener, OnPostExecuteListener {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var customerListAdapter: CustomerListAdapter
    private lateinit var savedFileUri: Uri
    private var customerList: ArrayList<Customer> = arrayListOf()
    private var invitedCustomerList: ArrayList<Customer> = arrayListOf()
    private var shouldShowViewFileAction = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        ExecuteJSONTask(this).execute(CUSTOMERS_LIST_URL)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        if (shouldShowViewFileAction) {
            menu?.get(0)?.isVisible = true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewFile -> {
                openFileInFolder(savedFileUri)
                return true
            }
            R.id.showAll -> {
                showAllList()
                return true
            }
            R.id.showFiltered -> {
                showFilteredList()
                return true
            }

            R.id.save -> {
                if (writePermissionGranted()) {
                    saveResult(getInvitedCustomerMap())
                } else {
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, REQUEST_CODE_PERMISSIONS)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (writePermissionGranted()) {
                saveResult(getInvitedCustomerMap())
            } else {
                Toast.makeText(this, "Write permission not granted by the user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun writePermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showFilteredList() {
        customerListAdapter.updateCustomersList(invitedCustomerList)
    }

    private fun showAllList() {
        customerListAdapter.updateCustomersList(customerList)
    }

    private fun initRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        customerListAdapter = CustomerListAdapter(customerList)
        recyclerView.adapter = customerListAdapter
    }

    override fun onPostExecute(customerList: ArrayList<Customer>?) {
        customerList?.let {
            this.customerList = it
            customerListAdapter.updateCustomersList(it)

            invitedCustomerList = getInvitedCustomerList(it)
        }
    }

    private fun getInvitedCustomerList(customerList: ArrayList<Customer>): ArrayList<Customer> {
        val invitedCustomers = arrayListOf<Customer>()

        for (customer in customerList) {
            val distance = DistanceUtil.getDistance(
                customer.longitude,
                customer.latitude
            )

            if (distance <= DISTANCE_CAP) {
                invitedCustomers.add(customer)
            }
        }

        return invitedCustomers
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

    private fun getInvitedCustomerMap(): Map<Int, String> {
        val invitedCustomers = mutableMapOf<Int, String>()

        for (customer in invitedCustomerList) {
            invitedCustomers[customer.userId] = customer.name
        }

        return invitedCustomers
    }

    override fun onScanCompleted(string: String?, uri: Uri?) {
        uri?.let {
            savedFileUri = it
            shouldShowViewFileAction = true
            invalidateOptionsMenu()
        }
    }

    private fun openFileInFolder(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = uri
            startActivity(intent)
        } catch (exception: ActivityNotFoundException) {
            exception.printStackTrace()
            Toast.makeText(this, "You do not have a TXT file reader installed on your device.",  Toast.LENGTH_LONG).show();
        }
    }
}
