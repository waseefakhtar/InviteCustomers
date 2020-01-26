package com.waseefakhtar.invitecustomers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.waseefakhtar.invitecustomers.R
import com.waseefakhtar.invitecustomers.data.Customer
import com.waseefakhtar.invitecustomers.util.DistanceUtil


class CustomerListAdapter constructor(private var customerList: ArrayList<Customer>) : RecyclerView.Adapter<CustomerListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = customerList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(customerList[position])
    }

    fun updateCustomersList(newList: ArrayList<Customer>) {
        customerList = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var customerName: TextView = view.findViewById(R.id.customerName)
        private var customerDistance: TextView = view.findViewById(R.id.customerLocation)

        fun bind(customer: Customer) {
            val distance = DistanceUtil.getDistance(customer.longitude, customer.latitude)

            customerName.text = customer.name
            customerDistance.text = "Lives ${distance.toInt()} km from Intercom Dublin."
        }
    }
}

