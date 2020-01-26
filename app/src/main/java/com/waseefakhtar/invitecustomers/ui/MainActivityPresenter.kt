package com.waseefakhtar.invitecustomers.ui

import android.net.Uri
import com.waseefakhtar.invitecustomers.data.Customer
import com.waseefakhtar.invitecustomers.network.ExecuteJSONTask
import com.waseefakhtar.invitecustomers.network.OnPostExecuteListener
import com.waseefakhtar.invitecustomers.util.DistanceUtil

private const val DISTANCE_CAP = 100.0
private const val CUSTOMERS_LIST_URL = "https://s3.amazonaws.com/intercom-take-home-test/customers.txt"

class MainActivityPresenter constructor(private val view: MainInterface.View) : MainInterface.Presenter, OnPostExecuteListener {

    private var customerList: ArrayList<Customer> = arrayListOf()
    private var invitedCustomerList: ArrayList<Customer> = arrayListOf()
    private var savedFileUri: Uri? = null

    init {
        view.initView()
    }

    override fun executeJsonTask() {
        ExecuteJSONTask(this).execute(CUSTOMERS_LIST_URL)
    }

    override fun onPostExecute(result: ArrayList<Customer>?) {
        result?.let {
            updateCustomerList(it)
            updateInvitedCustomerList(it)
        }
    }

    private fun updateCustomerList(customerList: ArrayList<Customer>) {
        this.customerList = customerList
        view.updateViewWith(customerList)

    }

    private fun updateInvitedCustomerList(customerList: ArrayList<Customer>) {
        for (customer in customerList) {
            val distance = DistanceUtil.getDistance(
                customer.longitude,
                customer.latitude
            )

            if (distance <= DISTANCE_CAP) {
                invitedCustomerList.add(customer)
            }
        }
    }

    override fun updateListWithAll() {
        view.updateViewWith(customerList)
    }

    override fun updateListWithInvited() {
        view.updateViewWith(invitedCustomerList)
    }

    override fun generateInvitedCustomerMap() {
        val invitedCustomers = mutableMapOf<Int, String>()

        for (customer in invitedCustomerList) {
            invitedCustomers[customer.userId] = customer.name
        }

        view.saveFileToDisk(invitedCustomers)
    }

    override fun updateFileUri(uri: Uri) {
        this.savedFileUri = uri
        view.invalidateMenuItems()
    }

    override fun generateFileUri() {
        savedFileUri?.let { view.openFileInFolder(it) }
    }
}
