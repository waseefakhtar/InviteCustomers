package com.waseefakhtar.invitecustomers.ui

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
import com.waseefakhtar.invitecustomers.R
import com.waseefakhtar.invitecustomers.adapter.CustomerListAdapter
import com.waseefakhtar.invitecustomers.data.Customer
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileWriter
import java.io.IOException


private const val REQUEST_CODE_PERMISSIONS = 0
private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

class MainActivity : AppCompatActivity(), MediaScannerConnection.OnScanCompletedListener,
    MainInterface.View {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var customerListAdapter: CustomerListAdapter

    private var presenter: MainActivityPresenter? = null

    private var shouldShowViewFileAction = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter =
            MainActivityPresenter(this)
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun onStart() {
        super.onStart()

        presenter?.executeJsonTask()
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
                presenter?.generateFileUri()
                return true
            }
            R.id.showAll -> {
                presenter?.updateListWithAll()
                return true
            }
            R.id.showFiltered -> {
                presenter?.updateListWithInvited()
                return true
            }

            R.id.save -> {
                if (writePermissionGranted()) {
                    presenter?.generateInvitedCustomerMap()
                } else {
                    ActivityCompat.requestPermissions(this,
                        REQUIRED_PERMISSION,
                        REQUEST_CODE_PERMISSIONS
                    )
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (writePermissionGranted()) {
                presenter?.generateInvitedCustomerMap()
            } else {
                Toast.makeText(this, "Write permission not granted by the user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun writePermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun initRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        customerListAdapter = CustomerListAdapter(arrayListOf())
        recyclerView.adapter = customerListAdapter
    }

    override fun updateViewWith(customerList: ArrayList<Customer>) {
        customerListAdapter.updateCustomersList(customerList)
    }

    override fun saveFileToDisk(customerMap: Map<Int, String>) {
        val directoryPath = Environment.getExternalStoragePublicDirectory("Intercom Party Invitations")

        if (!directoryPath.exists()) {
            directoryPath.mkdir()
        }

        val documentTitle = "output.txt"
        val filePath = File(directoryPath, documentTitle)

        MediaScannerConnection.scanFile(this, arrayOf(filePath.toString()), null, this)

        try {
            val writer = FileWriter(filePath)
            customerMap.forEach { writer.append("UserID: ${it.key} Customer Name: ${it.value}\n") }
            writer.flush()
            writer.close()

            Toast.makeText(this, "Text File successfully saved at location: ${directoryPath}!", Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            Log.e("main", "error $e");
            Toast.makeText(this, "Something went wrong: $e",  Toast.LENGTH_LONG).show();
        }
    }

    override fun onScanCompleted(string: String?, uri: Uri?) {
        uri?.let {
            presenter?.updateFileUri(it)
        }
    }

    override fun invalidateMenuItems() {
        shouldShowViewFileAction = true
        invalidateOptionsMenu()
    }

    override fun openFileInFolder(uri: Uri) {
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
