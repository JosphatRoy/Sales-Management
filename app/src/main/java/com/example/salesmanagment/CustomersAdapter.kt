package com.example.salesmanagment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class CustomersAdapter(
    private var customers: List<Customer>,
    private val onItemClick: (Customer) -> Unit
) : RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder>() {

    fun updateList(newList: List<Customer>) {
        customers = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customers[position]
        holder.bind(customer, onItemClick)
    }

    override fun getItemCount(): Int = customers.size

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCustomerName)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvCustomerPhone)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvCustomerEmail)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotalPurchases)

        fun bind(customer: Customer, onItemClick: (Customer) -> Unit) {
            tvName.text = customer.name
            tvPhone.text = customer.phone
            tvEmail.text = customer.email
            tvTotal.text = String.format(Locale.getDefault(), "$ %.2f", customer.totalPurchases)

            itemView.setOnClickListener {
                onItemClick(customer)
            }
        }
    }
}
