package com.waseefakhtar.invitecustomers

import android.net.Uri
import com.waseefakhtar.invitecustomers.data.Customer

interface MainInterface {

    interface View {
        fun initView()
        fun updateViewWith(customerList: ArrayList<Customer>)
        fun saveFileToDisk(customerMap: Map<Int, String>)
        fun openFileInFolder(uri: Uri)
        fun invalidateMenuItems()
    }

    interface Presenter {
        fun executeJsonTask()
        fun updateListWithAll()
        fun updateListWithInvited()
        fun generateInvitedCustomerMap()
        fun updateFileUri(uri: Uri)
        fun generateFileUri()
    }

}