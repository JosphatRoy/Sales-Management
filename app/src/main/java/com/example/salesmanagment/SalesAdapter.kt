package com.example.salesmanagment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class SalesAdapter(
    private var sales: List<Sale>,
    private val onItemClick: (Sale) -> Unit = {}
) : RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {

    fun updateList(newList: List<Sale>) {
        sales = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sale, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = sales[position]
        holder.bind(sale, onItemClick)
    }

    override fun getItemCount(): Int = sales.size

    class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvSaleProductName)
        private val tvDetails: TextView = itemView.findViewById(R.id.tvSaleDetails)
        private val tvCustomer: TextView = itemView.findViewById(R.id.tvSaleCustomer)
        private val tvPaymentMode: TextView = itemView.findViewById(R.id.tvSalePaymentMode)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvSaleTotal)

        fun bind(sale: Sale, onItemClick: (Sale) -> Unit) {
            tvProductName.text = sale.productName
            tvDetails.text = String.format(Locale.getDefault(), "%.0f x Ksh %.2f", sale.quantity, sale.unitPrice)
            tvCustomer.text = if (sale.customerName.isNullOrEmpty()) "Walk-in Customer" else "Customer: ${sale.customerName}"
            tvPaymentMode.text = "Mode: ${sale.paymentMode ?: "Not specified"}"
            tvTotal.text = String.format(Locale.getDefault(), "Ksh %.2f", sale.totalAmount)

            itemView.setOnClickListener { onItemClick(sale) }
        }
    }
}
